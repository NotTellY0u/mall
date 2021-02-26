package me.lin.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.product.dao.CategoryDao;
import me.lin.mall.product.entity.CategoryEntity;
import me.lin.mall.product.service.CategoryBrandRelationService;
import me.lin.mall.product.service.CategoryService;
import me.lin.mall.product.vo.Catalog2Vo;
import me.lin.mall.product.vo.Catalog3Vo;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    final
    CategoryDao categoryDao;

    final
    CategoryBrandRelationService categoryBrandRelationService;

    final
    RedissonClient redissonClient;

    private final StringRedisTemplate redisTemplate;

    public CategoryServiceImpl(CategoryDao categoryDao, CategoryBrandRelationService categoryBrandRelationService, StringRedisTemplate redisTemplate, RedissonClient redissonClient) {
        this.categoryDao = categoryDao;
        this.categoryBrandRelationService = categoryBrandRelationService;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2.组装父子的树形结构
        //2.1).找到所有的一级分类
        return entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).peek((menu) -> menu.setChildren(getChildren(menu, entities))).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();

        List<Long> parentPath = findParentPath(catelogId, path);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[0]);
    }

    /**
     * 级联更新分类
     *
     * @param category 分类名
     */
//    @CacheEvict(value = "category", key = "#root.method.name")
//    @Caching(evict = {
//            @CacheEvict(value = "category", key = "'getLevelOneCategorys'"),
//            @CacheEvict(value = "category", key = "'getCatalogJson'")})
    @CacheEvict(value ="category", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /*
      @Cacheable: 当前方法的结果需要缓存 并指定缓存名字
     *  缓存的value值 默认使用jdk序列化
     *  默认ttl时间 -1
     *	key: 里面默认会解析表达式 字符串用 ''
     *
     *  自定义:
     *  	1.指定生成缓存使用的key
     *  	2.指定缓存数据存活时间	[配置文件中修改]
     *  	3.将数据保存为json格式
     *
     *  sync = true: --- 开启同步锁

     */

    /**
     * 获取一级分类
     *
     * @return 一级分类列表
     */
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevelOneCategorys() {

        System.out.println("运行了getLevelOneCategorys");

        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", "0"));
    }

    /**
     * 获取json分类数据
     *
     * @return 分类json数据
     */
    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        //给缓存中放json字符串，拿出的json字符串，还能逆转为能用的对象类型[序列化与反序列化]

        /*
          1.空结果缓存：解决缓存穿透
          2.设置过期时间（加随机值）：解决缓存雪崩问题
          3.加锁：解锁缓存击穿问题
         */

        // 1.加入缓存逻辑,缓存中存的是json字符串
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            System.out.println("缓存未命中");
            //2. 缓存中没有，查询数据库
            //保证数据库查询完成后，将数据放在redis中，这是一个原子操作
            return getCatalogJsonFromDbWithRedisLock();
        }
        System.out.println("缓存命中");
        return JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
    }

    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库" + Thread.currentThread().getName());
        //得到锁以后，我们应该再去缓存中确定一次，过没有才需要继续查询
        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1 = getCategoryEntities(entityList, 0L);
        Map<String, List<Catalog2Vo>> collect = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 拿到每一个一级分类 然后查询他们的二级分类
            List<CategoryEntity> entities = getCategoryEntities(entityList, v.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (entities != null) {
                catalog2Vos = entities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类
                    List<CategoryEntity> level3 = getCategoryEntities(entityList, l2.getCatId());
                    // 三级分类有数据的情况下
                    if (level3 != null) {
                        List<Catalog3Vo> catalog3Vos = level3.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(catalog3Vos);
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));

        return collect;
    }

    /**
     * 从数据库查询并封装json分类数据
     *
     * @return 分类json数据
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {

        synchronized (this) {
            return getDataFromDb();
        }

    }

    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {


        // 1.锁的名字
        // 锁的粒度：具体缓存的是某个数据
        RLock lock = redissonClient.getLock("catalogJson-lcok");
        lock.lock();
        Map<String, List<Catalog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDb;
    }


    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        String uuid = UUID.randomUUID().toString();
        // 1.占分布式锁
        boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);

        if (lock) {
            System.out.println("获取分布式锁成功");
            //加锁成功，执行业务
            Map<String, List<Catalog2Vo>> dataFromDb = null;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return dataFromDb;
        } else {
            //加锁失败
            System.out.println("获取分布式锁不成功");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }


    }

    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isNotBlank(catalogJSON)) {
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库" + Thread.currentThread().getName());
        //得到锁以后，我们应该再去缓存中确定一次，过没有才需要继续查询
        List<CategoryEntity> entityList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1 = getCategoryEntities(entityList, 0L);
        Map<String, List<Catalog2Vo>> collect = level1.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 拿到每一个一级分类 然后查询他们的二级分类
            List<CategoryEntity> entities = getCategoryEntities(entityList, v.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (entities != null) {
                catalog2Vos = entities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), l2.getName(), l2.getCatId().toString(), null);
                    // 找当前二级分类的三级分类
                    List<CategoryEntity> level3 = getCategoryEntities(entityList, l2.getCatId());
                    // 三级分类有数据的情况下
                    if (level3 != null) {
                        List<Catalog3Vo> catalog3Vos = level3.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(), l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(catalog3Vos);
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));
        //3. 查到的数据再放入缓存
        String s = JSON.toJSONString(collect);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return collect;
    }

    /**
     * 第一次查询的所有 CategoryEntity 然后根据 parent_cid去这里找
     */
    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> entityList, Long parentCid) {

        return entityList.stream().filter(item -> parentCid.equals(item.getParentCid())).collect(Collectors.toList());
    }

    /**
     * 查找父节点
     *
     * @param catelogId 分类id
     * @param path      路径
     * @return 父节点列表
     */
    private List<Long> findParentPath(Long catelogId, List<Long> path) {
        // 1.收集当前节点id
        path.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), path);
        }
        return path;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> collect = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1.找到子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2.菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return collect;
    }

}
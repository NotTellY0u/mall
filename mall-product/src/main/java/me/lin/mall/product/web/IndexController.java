package me.lin.mall.product.web;

import me.lin.mall.product.entity.CategoryEntity;
import me.lin.mall.product.service.CategoryService;
import me.lin.mall.product.vo.Catalog2Vo;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author Fibonacci
 * @create: 2021-02-07 10:02
 * @Version 1.0
 */

@Controller
public class IndexController {

    private final RedissonClient redisson;

    final CategoryService categoryService;

    final
    StringRedisTemplate redisTemplate;

    public IndexController(CategoryService categoryService, StringRedisTemplate redisTemplate, RedissonClient redisson) {
        this.categoryService = categoryService;
        this.redisTemplate = redisTemplate;
        this.redisson = redisson;
    }

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {

        List<CategoryEntity> categoryEntityList = categoryService.getLevelOneCategorys();
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    @ResponseBody
    @RequestMapping("index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatlogJson() {

        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    /**
     * RLock锁有看门狗机制 会自动帮我们续期，默认三秒自动过期
     * lock.lock(10,TimeUnit.SECONDS); 十二猴子的时间一定要大于业务的时间 否则会出现死锁的情况
     * <p>
     * 如果我们传递了锁的超时时间就给redis发送超时脚本 默认超时时间就是我们指定的
     * 如果我们未指定，就使用 30 * 1000 [LockWatchdogTimeout]
     * 只要占锁成功 就会启动一个定时任务 任务就是重新给锁设置过期时间 这个时间还是 [LockWatchdogTimeout] 的时间 1/3 看门狗的时间续期一次 续成满时间
     */
    @ResponseBody
    @GetMapping("index/hello")
    public String hello(){
        RLock lock = redisson.getLock("my-lock");
        lock.lock();
        try {
            System.out.println("加锁成功，执行业务........"+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e) {

        }finally {
            System.out.println("释放锁....."+Thread.currentThread().getId());
            //3.解锁
            lock.unlock();
        }
        return "hello";
    }


    /**
     *   保证一定能读到最新数据，修改期间，写锁是一个排它锁（互斥锁）。读锁是一个共享锁
     *   写锁没释放，读就要一直等待
     */

    @GetMapping("/write")
    @ResponseBody
    public String writeValue(){

        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");

        String s = null;
        //改数据加写锁，读数据加读锁
        RLock rLock = lock.writeLock();

        try {
            rLock.lock();
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }
    @GetMapping("/read")
    @ResponseBody
    public String readValue(){
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s = null;
        RLock rLock = lock.readLock();
        rLock.lock();
        try {
            s = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();
        return "放假了";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id")Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();
        return id + "所有人都走了";
    }

}

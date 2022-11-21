package me.lin.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.lin.mall.common.utils.PageUtils;
import me.lin.mall.common.utils.Query;
import me.lin.mall.product.dao.SpuImagesDao;
import me.lin.mall.product.entity.SpuImagesEntity;
import me.lin.mall.product.service.SpuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存图片
     * @param id id
     * @param images 图片
     */
    @Override
    public void saveImages(Long id, List<String> images) {
       if(images == null || images.size() == 0){
        log.error("images为空");
       }else {
           List<SpuImagesEntity> collect = images.stream().map(img -> {
               SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
               spuImagesEntity.setSpuId(id);
               spuImagesEntity.setImgUrl(img);
               return spuImagesEntity;
           }).collect(Collectors.toList());
           this.saveBatch(collect);
       }
    }

}
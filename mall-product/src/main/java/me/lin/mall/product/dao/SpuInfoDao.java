package me.lin.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.lin.mall.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author Fibonacci
 * @email bugaosuni@gmail.com
 * @date 2020-12-11 14:38:50
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 更新spu状态
     * @param spuId spuId
     * @param code 状态码
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}

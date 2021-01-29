package me.lin.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import me.lin.mall.common.to.es.SkuEsModel;
import me.lin.mall.search.constant.EsConstant;
import me.lin.mall.search.service.ProductSaveService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Fibonacci
 * @create: 2021-01-27 16:26
 * @Version 1.0
 */
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Qualifier("esRestClient")
    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 保存上架状态
     *
     * @param skuEsModels 需要保存状态的商品
     */
    @Override
    public void productStatusUp(List<SkuEsModel> skuEsModels) {

        //保存到es
        // 1.给es中建立索引,product，建立映射关系

        // 2.给es中保存这些数据
        //BulkRequest\
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            // 构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(String.valueOf(skuEsModel.getSkuId()));
            String s = JSON.toJSONString(skuEsModel);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);


        }

    }
}

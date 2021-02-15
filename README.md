# mall
### OSS配置

nacos中新增thirdParty命名空间，在mall-third-party中的bootstrap.properties文件中配置
在mall-product中配置oss，如下：

 ```  cloud:
        nacos:
          discovery:
            server-addr: 127.0.0.1:8848
        alicloud:
          access-key: 
          secret-key: 
          oss:
            endpoint: 
 ```

### 日期格式调整
 ```
jackson:
    data-format: yyyy-MM-dd HH:mm:ss
  ```


### elasticsearch的安装

mkdir -p /mydocker/mall/elasticsearch/config

mkdir -p /mydocker/mall/elasticsearch/data

mkdir -p /mydocker/mall/elasticsearch/plugins

echo "http.host: 0.0.0.0" >> /mydocker/mall/elasticsearch/config/elasticsearch.yml

chmod -R 777 /mydocker/mall/elasticsearch/

win
```
docker run --name es -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms64m -Xmx512m" -v /D:/ElasticSearch/config:/usr/share/elasticsearch/config -v /D:/elasticsearch/data:/usr/share/elasticsearch/data -v /D:/ElasticSearch/plugins:/usr/share/elaticsearch/plugins -d elasticsearch:7.4.2
```

linux
```
docker run -d --name es -p 9200:9200 -p 9300:9300 -e ES_JAVA_OPTS="-Xms512m -Xmx512m" -v /mydocker/mall/elasticsearch/plugins:/usr/share/elasticsearch/plugins -v /mydocker/mall/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml -v /mydocker/mall/elasticsearch/data:/usr/share/elasticsearch/data -e "discovery.type=single-node" elasticsearch:7.4.2
```

### kibana的安装
docker run --name kibana -p 5601:5601 -d kibana:7.4.2


### elasticsearch查询

测试文档[开启科学上网]：https://raw.githubusercontent.com/elastic/elasticsearch/master/docs/src/test/resources/accounts.json

```
GET /custome/external
#批量插入
POST /custome/external/_bulk
{"index":{"_id":"1"}}
{"name":"lsl"}
{"index":{"_id":"2"}}
{"name":"firenay"}

#批量增删改
POST /_bulk
{"delete":{"_index":"website","_type":"blog","_id":"123"}}
{"create":{"_index":"website","_type":"blog","_id":"123"}}
{"title":"My first blog post"}
{"index":{"_index":"website","_type":"blog"}}
{"title":"My second blog post"}
{"update":{"_index":"website","_type":"blog","_id":"123"}}
{"doc":{"title":"My second blog post"}}

GET /_cat/indices/bank
POST /bank/account/_bulk

# 查询bank下所有数据并按照 account_number 进行升序排序 默认返回前条
GET bank/_search?q=*&sort=account_number:asc

# 账号相同的情况下 按照余额降序
GET /bank/_search
{
  "query": { "match_all": {} },
  "sort": [
    { "account_number": "asc" },
    {
      "balance":"desc"
    }
  ]
}

# 按照金额进行倒叙排序 从第二条记录开始只返回balance、firstname两个字段 每页大小为5
GET bank/_search
{
  "query": {
    "match_all": {} 
  },
  "size": 5,
  "from": 2, 
  "sort": [
    {
      "balance":{
        "order": "desc"
      }
    }
  ],
  "_source": [
    "balance","firstname"  
  ]
}

# 匹配account_number值为20的记录
GET bank/_search
{
  "query": {
    "match": {
      "account_number":"20"
    } 
  }
}

# 全文匹配模糊匹配 address
GET bank/_search
{
  "query": {
    "match": {
      "address":"mill lane"
    } 
  }
}

# 短词匹配 address  只匹配包这个的
GET bank/_search
{
  "query": {
    "match_phrase": {
      "address":"mill lane"
    } 
  }
}

# multi_match :只要再指定的地段中包含这个值就行 多字段匹配 会进行分词
GET bank/_search
{
  "query": {
    "multi_match": {
      "query":"mill movivo",
      "fields": ["address","city"]
    } 
  }
}

# 复合查询 必须满足的条件 必须不满足的条件 should：应该满足,也可也不满足 [这三都会贡献相关性得分 filter没有]
GET bank/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "address": "mill" } },
        { "match": { "gender": "M" } }
      ],
      "must_not": [
        { "match": { "state": "KY" } }
      ],
      "should": [
        {
          "match": {
            "lastname": "Wallace"
          }
        }
      ],
      "filter": {
        "range": {
          "age": {
            "gte": 10,
            "lte": 38
          }
        }
      }
    } 
  }
}

# range 区间查找 满足条件的相关性得分1.0
GET bank/_search
{
  "query": {
    "bool": {
      "must": [{ 
        "range": {
          "age":{
            "gte":10,
            "lte":30
          }
        }
      }]
    } 
  }
}

# filter 区间查找 满足条件的相关性得分0
GET bank/_search
{
  "query": {
    "bool": {
      "filter": [{ 
        "range": {
          "age":{
            "gte":10,
            "lte":30
          }
        }
      }]
    } 
  }
}

# balance之类的字段就用term查询 全文检索用match
# 精确值字段的用 term, 
GET bank/_search
{
  "query": {
    "term": {
      "age":"32838"
    } 
  }
}

# .keyword: 整个值作为查找的精确值
GET bank/_search
{
  "query": {
    "match": {
      "address.keyword":"789 Madison"
    } 
  }
}

# 这里的包含
GET bank/_search
{
  "query": {
    "match_phrase": {
      "address":"789 Madison"
    } 
  }
}

## 搜索address中包含所有人的年龄分布以及平均年龄
GET bank/_search
{
  "query": {
    "match": {
      "address": "mill"
    }
  },
  "aggs": {
    "ageAgg": {
      "terms": {
        "field": "age",
        "size": 10
      }
    },
    "ageAvg":{
      "avg": {
        "field": "age"
      }
    },
    "balanceAvg":{
      "avg": {
        "field": "balance"
      }
    }
  },
  "size": 0
}

# 先按照年龄进行聚合 再根据年龄段求出这个年龄的平均薪资
GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "aggs": {
    "ageAgg": {
      "terms": {
        "field": "age",
        "size": 100
      },
      "aggs": {
        "avgAgg": {
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  },
  "size": 0
}

# 先查出年龄分布，并且这些年龄段中M的平均薪资和平均薪资F的平均薪资以及这个年龄段的总体薪资

GET bank/_search
{
  "query": {
    "match_all": {}
  },
  "aggs": {
    "ageAgg": {
      "terms": {
        "field": "age",
        "size": 100
      },
      "aggs": {
        "genderAgg":{
          "terms": {
            "field": "gender.keyword"
          },
          "aggs": {
            "balanceAgg":{
              "avg": {
                "field": "balance"
              }
            }
          }
        },
        "ageBalanceAvg":{
          "avg": {
            "field": "balance"
          }
        }
      }
    }
  },
  "size": 0
}

# mappings 自定义类型
PUT /my_index
{
  "mappings": {
    "properties": {
      "age":{"type": "integer"},
      "email": {"type": "keyword"},
      "name":{"type": "text"}
    }
  }
}

# 查询这个映射的类型
GET /my_index/_mapping
# 为原有映射添加类型
PUT /my_index/_mapping
{
  "properties": {
    "employee-id":{
      "type": "keyword",
      "index": false
    }
  }
}

GET /bank/_mapping

# 创建一个新的索引 对数据进行迁移
GET /newbank/_mapping
PUT /newbank
{
  "mappings": {
    "properties": {
      "account_number": {
        "type": "long"
      },
      "address": {
        "type": "text"
      },
      "age": {
        "type": "integer"
      },
      "balance": {
        "type": "long"
      },
      "city": {
        "type": "keyword"
      },
      "email": {
        "type": "keyword"
      },
      "employer": {
        "type": "keyword"
      },
      "firstname": {
        "type": "text"
      },
      "gender": {
        "type": "keyword"
      },
      "lastname": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "state": {
        "type": "keyword"
      }
    }
  }
}

# 将bank里面的数据迁移到 newbank, 指定bank中的数据类型 [不再使用type]
GET bank/_search
POST _reindex
{
  "source": {
    "index": "bank", 
    "type": "account"
  },
  "dest": {
    "index": "newbank"
  }
}

GET newbank/_search

# 分词器
POST _analyze
{
  "analyzer": "ik_max_word",
  "text": "我很喜欢你"
}
```

### nginx安装

将容器里面的某个文件夹复制到当前文件夹

```
docker pull nginx:1.10

docker run -p 80:80 --name nginx -d nginx:1.10

cd /mydata

mkdir nginx

docker container cp 容器名:/etc/nginx .

mv nginx conf

mkdir nginx

mv conf nginx

docker stop nginx

docker rm nginx

win下
docker run -p 80:80 --name nginx -v /D/ElasticSearch/nginx/html:/usr/share/nginx/html -v /D/ElasticSearch/nginx/logs:/var/log/nginx -v /D/ElasticSearch/nginx/conf:/etc/nginx -d nginx:1.10

mac下
docker run -p 80:80 --name nginx -v /mydata/nginx/html:/usr/share/nginx/html -v /mydata/nginx/logs:/var/log/nginx -v /mydata/nginx/conf:/etc/nginx -d nginx:1.10

```

### nginx配置

```

cd nginx/conf

vi nginx.conf

添加：    
    upstream mall{
        server 10.211.55.2:88;
    }

cd conf.d

cp default.conf mall.conf

vi mall.conf

修改:
   location / {
        proxy_pass http://mall;
    }


```

### 项目PUT索引:

```
# nested : 嵌入式数据类型

PUT product
{
  "mappings": {
    "properties": {
      "skuId":{
        "type": "long"
      },
      "spuId":{
        "type": "keyword"
      },
      "skuTitle":{
        "type": "text",
        "analyzer": "ik_smart"
      },
      "skuPrice":{
        "type": "keyword"
      },
      "skuImg":{
        "type": "keyword"
      },
      "saleCount":{
        "type": "long"
      },
      "hasStock":{
        "type": "boolean"
      },
      "hotScore":{
        "type": "long"
      },
      "brandId":{
        "type": "long"
      },
      "catalogId":{
        "type": "long"
      },
      "brandName":{
        "type":"keyword"
      },
      "brandImg":{
        "type": "keyword"
      },
      "catalogName":{
        "type": "keyword"
      },
      "attrs":{
        "type": "nested",
        "properties": {
          "attrId":{
            "type":"long"
          },
          "attrName":{
            "type":"keyword"
          },
          "attrValue":{
            "type":"keyword"
          }
        }
      }
    }
  }
}
```

### windows下短时间高并发测试报端口占用问题

进入注册表：regedit

计算机\HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters

创建这两个

```
MaxUserPort 			十进制值：65534
TCPTimedWaitDelay		 十进制值：30
```

![](.\img\端口占用问题1.png)

调整项目运行时内存：

最大最小内存1024MB ,新生代设置为512. 因为这个服务临时对象非常多

-Xmx1024m -Xms1024m -Xmn512m

### ES嵌入式查询

```
# 嵌入查询
GET product/_search
{
  "query": {
    "bool": {
      "filter": {
        "nested": {
          "path": "attrs",
          "query": {
            "bool": {
              "must": [
                {
                  "term": {
                    "attrs.attrId": {
                      "value": "12"
                    }
                  }
                },
                {
                  "terms": {
                    "attrs.attrValue": [
                      "麒麟990",
                      "海思(Hisilicon)"
                    ]
                  }
                }
              ]
            }
          }
        }
      }
    }
  }
}
```

### ES复合查询

```
# 指定catalogId 、brandId、attrs.attrId、嵌入式查询、倒序、0-6000、每页显示两个、高亮skuTitle、聚合分析
GET product/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "skuTitle": "华为"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "catalogId": "225"
          }
        },
        {
          "terms": {
            "brandId": [
              "1",
              "2",
              "3"
            ]
          }
        },
        {
          "nested": {
            "path": "attrs",
            "query": {
              "bool": {
                "must": [
                  {
                    "term": {
                      "attrs.attrId": {
                        "value": "12"
                      }
                    }
                  },
                  {
                    "terms": {
                      "attrs.attrValue": [
                        "麒麟990",
                        "海思(Hisilicon)"
                      ]
                    }
                  }
                ]
              }
            }
          }
        },
        {
          "term": {
            "hasStock": {
              "value": "true"
            }
          }
        },
        {
          "range": {
            "skuPrice": {
              "gte": 0,
              "lte": 6000
            }
          }
        }
      ]
    }
  },
  "sort": [
    {
      "skuPrice": {
        "order": "desc"
      }
    }
  ],
  "from": 0,
  "size": 2,
  "highlight": {
    "fields": {
      "skuTitle": {}
    },
    "pre_tags": "<b style='color:red'>",
    "post_tags": "</b>"
  },
  "aggs": {
    "brand_agg": {
      "terms": {
        "field": "brandId",
        "size": 10
      },
      "aggs": {
        "brand_name_agg": {
          "terms": {
            "field": "brandName",
            "size": 10
          }
        },
        "brand_img_agg": {
          "terms": {
            "field": "brandImg",
            "size": 10
          }
        }
      }
    },
    "catalog_agg": {
      "terms": {
        "field": "catalogId",
        "size": 10
      },
      "aggs": {
        "catalog_name_agg": {
          "terms": {
            "field": "catalogName",
            "size": 10
          }
        }
      }
    },
    "attr_agg": {
      "nested": {
        "path": "attrs"
      },
      "aggs": {
        "attr_id_agg": {
          "terms": {
            "field": "attrs.attrId",
            "size": 10
          },
          "aggs": {
            "attr_name_agg": {
              "terms": {
                "field": "attrs.attrName",
                "size": 10
              }
            },
            "attr_value_agg":{
              "terms": {
                "field": "attrs.attrValue",
                "size": 10
              }
            }
          }
        }
      }
    }
  }
}
```

### ES数据迁移

```
POST _reindex
{
  "source": {
    "index": "poduct"
  },
  "dest": {
    "index": "glmall_product"
  }
}
```

### 聚合、子聚合

```
# 聚合、子聚合、聚合 嵌入式的属性：所有的操作都应该用嵌入式的
GET product/_search
{
  "query": {
    "match_all": {}
  },
  "size": 0,
  "aggs": {
    "brand_agg": {
      "terms": {
        "field": "brandId",
        "size": 10
      },
      "aggs": {
        "brand_name_agg": {
          "terms": {
            "field": "brandName",
            "size": 10
          }
        },
        "brand_img_agg": {
          "terms": {
            "field": "brandImg",
            "size": 10
          }
        }
      }
    },
    "catalog_agg": {
      "terms": {
        "field": "catalogId",
        "size": 10
      },
      "aggs": {
        "catalog_name_agg": {
          "terms": {
            "field": "catalogName",
            "size": 10
          }
        }
      }
    },
    "attr_agg": {
      "nested": {
        "path": "attrs"
      },
      "aggs": {
        "attr_id_agg": {
          "terms": {
            "field": "attrs.attrId",
            "size": 10
          },
          "aggs": {
            "attr_name_agg": {
              "terms": {
                "field": "attrs.attrName",
                "size": 10
              }
            },
            "attr_value_agg":{
              "terms": {
                "field": "attrs.attrValue",
                "size": 10
              }
            }
          }
        }
      }
    }
  }
}
```

### 联合查询

```
SELECT pav.`spu_id`, ag.`attr_group_name`, ag.`attr_group_id`, aar.`attr_id`, attr.`attr_name` ,pav.`attr_value`
        FROM `pms_attr_group` ag
        LEFT JOIN `pms_attr_attrgroup_relation` aar ON aar.`attr_group_id` = ag.`attr_group_id`
        LEFT JOIN `pms_attr` attr ON attr.`attr_id` = aar.`attr_id`
        LEFT JOIN `pms_product_attr_value` pav ON pav.`attr_id` = attr.`attr_id`
        WHERE ag.catelog_id = 225 AND pav.`spu_id` = 3;
```

```
SELECT ssav.`attr_id` attr_id,ssav.`attr_name` attr_name,GROUP_CONCAT(DISTINCT ssav.`attr_value`) attr_values FROM `pms_sku_info` INFO LEFT JOIN `pms_sku_sale_attr_value` ssav ON ssav.`sku_id` = info.`sku_id` WHERE info.`spu_id` = 3 GROUP BY ssav.`attr_id`, ssav.`attr_name`;
```

普通查询：各个属性对应的skuId查询出来

```
SELECT ssav.`attr_id`,ssav.`attr_name`,ssav.`attr_value`,GROUP_CONCAT(DISTINCT info.`sku_id`) FROM `pms_sku_info` INFO LEFT JOIN `pms_sku_sale_attr_value` ssav ON ssav.`sku_id` = info.`sku_id` WHERE info.`spu_id` = 3 GROUP BY  ssav.`attr_id`,ssav.`attr_name`,ssav.`attr_value`;
```



### rabbitmq安装

```
docker run -d --name rabbitmq -p 5671:5671 -p 5672:5672 -p 4369:4369 -p 25672:25672 -p 15671:15671 -p 15672:15672 rabbitmq:management
```

自动重启：docker update rabbitmq --restart=always

### Feign远程调用丢失请求头问题

```
浏览器发送请求自动带了Cookie Feign远程调用会创建一个新的请求头 Cart服务发现这个请求没有请求头[Session] Feign会执行所有的拦截器 .. 就会认为没有登录 最终导致访问失败
```

Lua脚本：

```
if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end
```

Seata表

```
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```

### MQ发送消息日志存储表

```
CREATE TABLE `mq_message`(`message_id` CHAR(32) NOT NULL, `content` TEXT COMMENT 'JSON', `to_exchange` VARCHAR(255) DEFAULT NULL, `class_type` VARCHAR(255) DEFAULT NULL, `message_status` INT(1) DEFAULT '0' COMMENT '0-新建 1-已发送 2-错误抵达 3-已抵达', `create_time` DATETIME DEFAULT NULL, `update_time` DATETIME DEFAULT NULL,PRIMARY KEY (`message_id`))ENGINE=INNODB DEFAULT CHARSET=utf8mb4;
```

### RabbitMQ中的Order服务启动不来解决方案

```
启动报错去ware服务的测试类运行所有代码
```

### zipKin安装

```
docker run -d -p 9411:9411 openzipkin/zipkin
```


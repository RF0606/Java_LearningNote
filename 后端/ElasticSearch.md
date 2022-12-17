# ElasticSearch

## 1. 下载及安装

[ES官网](https://www.elastic.co/cn/)

[ik插件](https://github.com/medcl/elasticsearch-analysis-ik/tree/master)

解压即安装

可视化插件在chrome插件商店搜索ElasticSearch Head并安装

解压ik到es的plugins中

## 2. 配置

```bash
# jvm.options
# 内存限制
-Xms1g
-Xmx1g

# elasticsearch.yml
# 默认端口9200 集群配置 跨域配置
# 跨域
http.cors.enabled: true
http.cors.allow-origin: "*"

# kibana.yml
# 默认端口5601
# 汉化
i18n.locale: "zh-CN"
```

## 3. ik分词器

在kibana的dev tools中测试

```bash
# ik分词器

# ik_smart 最少切分
GET _analyze
{
  "analyzer": "ik_smart",
  "text": "北京大学"
}
# 得到
{
  "tokens" : [
    {
      "token" : "北京大学",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 0
    }
  ]
}

# ik_max_word 最细粒度划分
GET _analyze
{
  "analyzer": "ik_max_word",
  "text": "北京大学"
}
# 得到
{
  "tokens" : [
    {
      "token" : "北京大学",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 0
    },
    {
      "token" : "北京大",
      "start_offset" : 0,
      "end_offset" : 3,
      "type" : "CN_WORD",
      "position" : 1
    },
    {
      "token" : "北京",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "大学",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 3
    }
  ]
}
```

自己扩展字典

在ik的config中

创建my.dic一行一个词

```bash
米哈游
原神
原神米哈游
```

在IKAnalyzer.cfg.xml中配置

```xml
<!--用户可以在这里配置自己的扩展字典 -->
<entry key="ext_dict">my.dic</entry>
```

## 3. Rest风格操作

| Methord |                     url地址                     |         描述         |
| :-----: | :---------------------------------------------: | :------------------: |
|   PUT   |     localhost:9200/索引名称/类型名称/文档id     | 创建文档(指定文档id) |
|  POST   |        localhost:9200/索引名称/类型名称         | 创建文档(随机文档id) |
|  POST   | localhost:9200/索引名称/类型名称/文档id/_update |       修改文档       |
| DELETE  |     localhost:9200/索引名称/类型名称/文档id     |       删除文档       |
|   GET   |     localhost:9200/索引名称/类型名称/文档id     |  查询文档通过文档id  |
|  POST   |    localhost:9200/索引名称/类型名称/_search     |     查询所有数据     |

- 字符串类型text, keyword
- 数值类型long, integer, short, byte, double, float, half float, scaled float
- 日期类型date
- 布尔值类型boolean
- 二进制类型binary.等等…

```bash
# 创建索引
PUT /test1/_doc/1
{
  "name": "狂神说",
  "age": 3
}

# 创建索引规则
PUT /test2
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "age": {
        "type": "long"
      },
      "birthday": {
        "type": "date"
      }
    }
    
  }
}

# 获取索引信息
GET /test2

# 获取ES情况
GET _cat/

# 通过PUT覆盖修改 不要用
PUT /test1/_doc/1
{
  "name": "嘿嘿嘿",
  "age": 3
}

# 通过POST更新修改
POST /test1/_doc/1/_update
{
  "doc": {
    "name": "asdfasdf"
  }
}

# 删除索引或者文档
DELETE /test1
DELETE /test1/_doc/1
```

## 4. 文档操作

创建索引

```bash
PUT /testtest/_doc/1
{
  "name": "狂神说",
  "age": 13,
  "desc": "在知识的海洋里,我竟然是一条淡水鱼",
  "tags": ["spring","springBoot","mybatis"]
}

PUT /testtest/_doc/2
{
  "name": "张三",
  "age": 13,
  "desc": "法外狂徒",
  "tags": ["Elasticsearch","springMVC","mybatis"]
}

PUT /testtest/_doc/3
{
  "name": "李四",
  "age": 1,
  "desc": "普通人",
  "tags": ["Vue.js","node.js","ES6"]
}

PUT /testtest/_doc/4
{
  "name": "狂神说Java",
  "age": 13,
  "desc": "在知识的海洋里,我竟然是一条淡水鱼",
  "tags": ["spring","springBoot","mybatis"]
}
```

```bash
# 根据文档id查询文档
GET /testtest/_doc/1

# 条件模糊查询
GET  /testtest/_doc/_search?q=name:狂神说

# 模糊查询 分词匹配 目标的分词
GET /testtest/_doc/_search
{
  "query": {
    "match": {
      "name": "狂神"
    }
  }
}

# 精确查询 不分词去匹配 被查目标如果是text会被分词 查询狂神 目标狂神 如果没有扩展词库会查询不到 如果是keyword就不会被分词
GET /testtest/_doc/_search
{
  "query": {
    "term": {
      "name": "狂神"
    }
  }
}

# 字段过滤 includes保留 excludes排除
GET /testtest/_doc/_search
{
  "query": {
    "match": {
      "name": "狂神"
    }
  },
  "_source": {
    "includes": [
      "name","age"
    ]
  }
}

# 排序
GET /testtest/_doc/_search
{
  "query": {
    "match": {
      "name": "狂神"
    }
  },
  "sort": {
    "_id": {
      "order": "asc"
    }
  }
}

# 分页 from从第几个开始 size查多少个
GET /testtest/_doc/_search
{
  "query": {
    "match": {
      "name": "狂神"
    }
  },
  "from": 0,
  "size": 1
}

# 布尔查询 must=and should=or must_not=not
GET /testtest/_doc/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "name": "狂神"
          }
        }, {
          "match": {
            "age": 14
          }
        }
      ]
    }
  }
}

# 过滤条件 lt小于 lte小于等于 gt大于 gte大于等于
GET /testtest/_doc/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "name": "狂神"
          }
        }
      ],
      "filter": {
        "range": {
          "age": {
            "lt": 20,
            "gt": 10
          }
        }
      }
    }
  }
}

# 高亮
GET /testtest/_doc/_search
{
  "from": 0,
  "size": 10,
  "query": {
    "match": {
      "name": "狂神"
    }
  },
  "highlight": {
    "fields": {
      "name": {}
    }
  }
}

# 自定义高亮
GET /testtest/_doc/_search
{
  "query": {
    "match": {
      "name": "狂神说"
    }
  },
  "highlight": {
    "pre_tags": "<p class='key' style='color: red'>",
    "post_tags": "</p>",
    "fields": {
      "name": {}
    }
  }
}
```

## 5. Springboot整合

在创建时选择NoSQL中的ElasticSearch依赖或者手动导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

将client版本改一致

```xml
<elasticsearch.version>7.17.0</elasticsearch.version>
```

在config中创建EsConfig

```java
@Configuration
public class EsConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http"))); // 集群的话 new多个
        return client;
    }
}
```

测试

装配

```java
@Autowired
RestHighLevelClient restHighLevelClient;
```

创建索引

```java
@Test
void createIndex() throws IOException {
    CreateIndexRequest request = new CreateIndexRequest("java_index");
    CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    System.out.println(response);
}
```

判断索引是否存在

```java
@Test
void existIndex() throws IOException {
    GetIndexRequest request = new GetIndexRequest("java_index");
    boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    System.out.println(exists);
}
```

删除索引

```java
@Test
void deleteIndex() throws IOException {
    DeleteIndexRequest request = new DeleteIndexRequest("java_index");
    AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
    System.out.println(response.isAcknowledged());
}
```

添加文档

```java
@Test
    void addDocument() throws IOException {
        User user = new User("嘿嘿嘿", 5);
        IndexRequest request = new IndexRequest("java_index");
        request.id("1");
//        request.timeout();
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }
```

判断文档是否存在

```java
@Test
void existsDocument() throws IOException {
    GetRequest request = new GetRequest("java_index", "1");
    boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
    System.out.println(exists);
}
```

获取文档

```java
@Test
void getDocument() throws IOException {
    GetRequest request = new GetRequest("java_index", "1");
    GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
    System.out.println(response.getSource());
    System.out.println(response.getVersion());
}
```

修改文档

```java
@Test
void updateDocument() throws IOException {
    UpdateRequest request = new UpdateRequest("java_index", "1");
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", "哈哈哈");
    request.doc(jsonObject.toJSONString(), XContentType.JSON);
    UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
    System.out.println(update.status());
}
```

删除文档

```java
@Test
void deleteDocument() throws IOException {
    DeleteRequest request = new DeleteRequest("java_index", "1");
    DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
    System.out.println(response.status());
}
```

批量请求

```java
@Test
void bulk() throws IOException {
    BulkRequest request = new BulkRequest();
    List<User> list = new ArrayList<>();
    list.add(new User("去", 1));
    list.add(new User("万", 2));
    list.add(new User("恶", 3));
    list.add(new User("让", 4));
    list.add(new User("他", 5));
    list.add(new User("要", 6));
    for (User user : list) {
        request.add(new IndexRequest("java_index").source(JSON.toJSONString(user), XContentType.JSON));
    }
    BulkResponse bulk = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    System.out.println(bulk.hasFailures());
}
```

查询文档

```java
@Test
void search() throws IOException {
    SearchRequest request = new SearchRequest("java_index");
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    MatchQueryBuilder query = QueryBuilders.matchQuery("name", "去");
    sourceBuilder.query(query);
    request.source(sourceBuilder);
    SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
    System.out.println(JSON.toJSONString(response.getHits()));
}
```

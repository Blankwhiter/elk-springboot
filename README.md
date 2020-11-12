# elk-springboot
1.elk-tcp模块在测试在src/test下进行测试


2.基于elasticsearch 实现crud
环境搭建
```
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300  -e "discovery.type=single-node" -v /home/software/elasticsearch/plugins:/usr/share/elasticsearch/plugins  -v /home/software/elasticsearch/data:/usr/share/elasticsearch/data -v /home/software/elasticsearch/config/es-single.yml:/usr/share/elasticsearch/config/elasticsearch.yml --name elasticsearch-single elasticsearch:7.9.3
```
注：将需要的插件下载并放置于 /home/software/elasticsearch/plugins 目录下，常用的用中文分词，已经拼音


基于spring-boot 2.3.3版本
```
    <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>2.3.3.RELEASE</version>
      </parent>
```    

以及elasticsearch 7.9.3版本
```
   <properties>
        <java.version>1.8</java.version>
        <elasticsearch.version>7.9.3</elasticsearch.version>
    </properties>

```
添加对应依赖
```
   <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>

```



springboot配置
ElasticSearchConfig.java
```java
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *  ElasticSearch配置
 */
@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
 
    /**
     * 高版本需声明RestHighLevelClient， ElasticsearchRestTemplate并需声明"elasticsearchOperations", "elasticsearchTemplate"
     * @return
     */
    @Bean(name = { "elasticsearchOperations", "elasticsearchTemplate" })
    public ElasticsearchRestTemplate elasticsearchRestTemplate(){
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }


    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("47.99.200.71:9200")
                .withHeaders(()->{
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("currentTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return headers;
                })
                .withConnectTimeout(Duration.ofSeconds(30))
                .withSocketTimeout(Duration.ofSeconds(15))
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}

```


创建User实体
```java
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 标记文档
 */
@Document(indexName = "test-use", type = "test-us")
public class User implements Serializable {

    /**
     * 标记id 必需要有
     */
    @Id
    private Long id;
    /**
     * 设置分词
     */
    @Field(name = "user_name", type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
    private String userName;

    /**
     * 设置分词
     */
    @Field(name = "desc", type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
    private String desc;

    /**
     * 自动检测类型
     */
    @Field(type = FieldType.Auto)
    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}


```


创建对应dao
```java
import com.elk.elktcp.entity.User;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserDao extends ElasticsearchRepository<User,Long> {
    @Highlight(fields = {
            @HighlightField(name = "userName", parameters = @HighlightParameters(
                    preTags = "<strong>",
                    postTags = "</strong>",
                    fragmentSize = 500,
                    numberOfFragments = 3))

    })
    List<SearchHit<User>> findByUserNameLike(String userName);



    @Highlight(fields = {
            @HighlightField(name = "desc", parameters = @HighlightParameters(
                    preTags = "<strong>",
                    postTags = "</strong>",
                    fragmentSize = 500,
                    numberOfFragments = 3))

    })
    List<SearchHit<User>> findByDescLike(String desc);

}

```


ElasticsearchRestTemplate
```java
 /**
     * BoolQuery( ) 用于组合多个叶子或复合查询子句的默认查询
     * must 相当于 与 & =
     * must not 相当于 非 ~   ！=
     * should 相当于 或  |   or
     * filter  过滤
     */

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @RequestMapping("member_search")
    public Response memberSearch() {
        //BoolQueryBuilder用来组装查询的条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //组装年龄条件
        RangeQueryBuilder age = QueryBuilders.rangeQuery("age");
        //年龄小于20的
        RangeQueryBuilder lt = age.gte(10).lte(100);
        //组装时间条件
        RangeQueryBuilder createTime = QueryBuilders.rangeQuery("create_time");
        //组装创建时间大于等于"2020-09-16 11:40:27小于等于"2020-09-17 11:40:43"的条件
        RangeQueryBuilder timeRange = createTime
                .format("yyyy-MM-dd HH:mm:ss")
                .gte("2019-09-10 11:40:27")
                .lte("2020-09-20 11:40:43");
        //将组装完的条件用queryBuilder.must()进行组合
        BoolQueryBuilder resultQuery = queryBuilder
                .must(lt)
                .must(timeRange);
        //NativeSearchQueryBuilder用来进行查询的调整比如排序,分页等;
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        NativeSearchQuery nativeSearchQuery = builder
                .withFilter(resultQuery)//关联查询条件
                .withPageable(PageRequest.of(0, 10))//分页条件
                .withSort(SortBuilders.fieldSort("create_time.keyword").order(SortOrder.ASC))
                .build();
        SearchHits<MemberInfo> memberInfoSearchHits = elasticsearchRestTemplate.search(nativeSearchQuery, MemberInfo.class, IndexCoordinates.of("t_member"));
        //获取得到的数据集合
        List<MemberInfo> memberInfos = memberInfoSearchHits.toList().parallelStream().map(SearchHit::getContent).collect(Collectors.toList());
        PageInfo<MemberInfo> pageInfo = new PageInfo<>(memberInfos);
        pageInfo.setTotal(memberInfoSearchHits.getTotalHits());
        return Response.success(pageInfo);
    }
```


附录：
基本API
1.创建索引
```PUT /index```

2.删除索引
```DELETE /index```

3.新建文档 建立索引
```
PUT /index/type/id
{
    json数据
}
```
4.检索文档
```GET /index/type/id```

5.修改文档信息
5.1 替换（覆盖）同样的id会覆盖之前放入的信息（必须带上所有的field，才能去进行信息的修改，否则只会把带的信息存储进去，其他数据丢失）
```
PUT /index/type/id
{
    json数据
}
```
5.2 更新文档
```
POST /index/type/id/_update
{
    json数据
}
```
6.删除文档
```DELETE /index/type/id```

7.查询index/type下全部数据
```GET /index/type/_search```

8.查询index/type下a字段包含xxx，并按照c字段降序排序
```GET /index/type/_search?q=a:xxx&sort=c:desc```

DSL(Domain Specified Language，特定领域的语言)
1.查询index/type下的全部数据
```
GET /index/type/_search
{
    "query":{"match_all":{}}
}
```
2.查询index/type下a字段包含xxx，并按照c字段降序排序
```
GET /index/type/_search
{
    "query":{
        "match":{
            "a":"xxx"
        }
    },
    "sort":[
        {"c":"desc"}
    ]
}
```

3.分页查询index/type下的全部数据
```
GET /index/type/_search
{
    "query":{"match_all":{}},
    "form":1,
    "size":10
}
```
4.查询index/type下的指定字段[a,b,c]的全部数据
```
GET /index/type/_search
{
    "query":{"match_all":{}},
    "_source":["a","b","c"]
}
```

5.搜索index/type下a字段包含xxx和yyy的数据（全文检索）
```
GET /index/type/_search
{
    "query":{
        "bool":{
            "must":{
                "match":{
                    "a":"xxx yyy"
                        }
                  }
              }     
    }
}
```


6.搜索index/type下a字段包含xxx，而且b字段大于100的数据
```
GET /index/type/_search
{
    "query":{
        "bool":{
            "must":{
                "match":{
                    "a":"xxx"
                        }
                  }
              },
        "filter":{
                "range":{
                       "b":{ "gt":100 }
                        }
                }      
    }
}
```

7.搜索index/type下a字段包含xxx yyy短语的数据(phrase search，要求输入的搜索串，必须在指定的字段文本中，完全包含一模一样的，才可以算匹配，才能作为结果返回)
```
GET /index/type/_search
{
    "query":{
        "match_phrase":{
            "a":"xxx yyy"
        }
    }
}
```
8.高亮搜索index/type下a字段包含xxx的数据
```
GET /index/type/_search
{
    "query":{
        "match":{
            "a":"xxx"
        }
    },
    "highlight":{
        "fields":{
            "a":{}
        }
    }
}
```

聚合查询
```
- size： 查询条数，这里设置为0，因为我们不关心搜索到的数据，只关心聚合结果，提高效率
- aggs：声明这是一个聚合查询，是aggregations的缩写
　　- popular_colors：给这次聚合起一个名字，任意。
　　　　- terms：划分桶的方式，这里是根据词条划分
　　　　　　- field：划分桶的字段

```


```
- hits：查询结果为空，因为我们设置了size为0
- aggregations：聚合的结果
　　- brand_aggs：我们定义的聚合名称
　　　　- buckets：查找到的桶，每个不同的brand字段值都会形成一个桶
　　　　　　- key：这个桶对应的brand字段的值
　　　　　　- doc_count：这个桶中的文档数量

```

桶内度量 

```
- aggs：我们在上一个aggs(brand_aggs)中添加新的aggs。可见度量也是一个聚合
　　- price_aggs：聚合的名称
　　　　- avg：度量的类型，这里是求平均值
　　　　　　- field：度量运算的字段
```



ik配置文件地址：es/plugins/ik/config目录
```
IKAnalyzer.cfg.xml：用来配置自定义词库

main.dic：ik原生内置的中文词库，总共有27万多条，只要是这些单词，都会被分在一起

quantifier.dic：放了一些单位相关的词

suffix.dic：放了一些后缀

surname.dic：中国的姓氏

stopword.dic：英文停用词
```

自定义分词步骤：
```
新增一个z_SelfAdd.dic文件，在里面加上新的单词，保存为UTF-8
然后在当前目录下的IKAnalyzer.cfg.xml配置文件中下加上<entry key="ext_dict">z_SelfAdd.dic</entry>
将刚才命名的文件加入
重启服务器，就生效了
```

edge_ngram是从第一个字符开始,按照步长,进行分词,适合前缀匹配场景,比如:订单号,手机号,邮政编码的检索
ngram是从每一个字符开始,按照步长,进行分词,适合前缀中缀检索


使用sql语句查询工具
`https://github.com/NLPchina/elasticsearch-sql`
离线安装：查找对应elasticsearch版本下载，放置于es的plugins的目录下

使用post请求，内容json形式
```
http://ip:9200/_nlpcn/sql
{
	"sql":"select * from indexName  limit 10"
}```

```
```
1）条件查询
SELECT * FROM bank WHERE age >30 AND gender = 'm'

2)聚合
select COUNT(*),SUM(age),MIN(age) as m, MAX(age),AVG(age)
  FROM bank GROUP BY gender ORDER BY SUM(age), m DESC
  
3)删除
DELETE FROM bank WHERE age >30 AND gender = 'm'

4)geo地理坐标
SELECT * FROM locations WHERE GEO_BOUNDING_BOX(fieldname,100.0,1.0,101,0.0)

5)需要指定index+type
SELECT * FROM indexName/type

6)如何指定路由
select /*! ROUTINGS(salary) */ sum(count)  from index where type="salary"

```

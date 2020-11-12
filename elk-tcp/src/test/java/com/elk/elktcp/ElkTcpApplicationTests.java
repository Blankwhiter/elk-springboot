package com.elk.elktcp;

import com.elk.elktcp.dao.ArticleDao;
import com.elk.elktcp.dao.UserDao;
import com.elk.elktcp.entity.Article;
import com.elk.elktcp.entity.Log;
import com.elk.elktcp.entity.User;
import com.elk.elktcp.service.LogService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElkTcpApplicationTests {

    @Value("${LOGSTASH_HOST}")
    private String ip;

    private final static Logger log = LoggerFactory.getLogger("newtest");

    @Test
    public void test() {
        log.info("ip:{}的filebeat kafka logstash   测试 info 成功了！！！", ip);
        log.warn("ip:{}的filebeat kafka logstash   测试 warn 成功了！！！", ip);
        log.error("ip:{}的filebeat kafka logstash   测试 error 成功了！！", ip);
    }


    @Autowired
    @Qualifier("elasticsearchTemplate")
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private LogService logService;

    @Test
    public void testElasticSearchService() {
        List<SearchHit<Log>> list = logService.findByMethodNameLike("test");
        System.out.println(list.size());
        for (int i = 0; i < 10; i++) {
            Log log = list.get(i).getContent();
            List<String> methodName = list.get(i).getHighlightField("methodName");
            System.out.println(log.getBeanName() + " : " + log.getMethodName());
        }
    }


    @Test
    public void testElasticSearchTemplate() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        NativeSearchQuery searchQuery = new NativeSearchQuery(boolQueryBuilder);
        SearchHits<Log> search = elasticsearchRestTemplate.search(searchQuery, Log.class);
        System.out.println(search.getTotalHits());
    }

    @Autowired
    private UserDao userDao;

    @Test
    public void testInsert() {
        User user = new User();
        user.setId(1L);
        user.setAge(30);
        user.setDesc("这个是描述，测试使用@，test1");
        user.setUserName("黄斌龙123");
        userDao.save(user);

        User user1 = new User();
        user1.setId(2L);
        user1.setAge(40);
        user1.setDesc("这个是描述，测试使用@，test，挺好1");
        user1.setUserName("黄斌龙13");
        userDao.save(user1);

    }

    @Test
    public void testUpdate() {
        //由于ElasticsearchRepository没有提供部分更新，所以先查询出记录信息，并更新对应字段
        Optional<User> user = userDao.findById(2L);
        user.ifPresent(u -> {
            u.setAge(50);
            u.setUserName("黄斌龙");
            userDao.save(u);
        });
    }

    @Test
    public void testDelete() {
        userDao.deleteById(2L);
    }

    @Test
    public void testSelect() {
//        List<SearchHit<User>> list = userDao.findByUserNameLike("黄");
//        System.out.println(list.size());
//
        List<SearchHit<User>> list = userDao.findByDescLike("庭");
        List<SearchHit<User>> list1 = userDao.findByDescLike("ting好");
        System.out.println(list.size());
    }


    @Autowired
    private ArticleDao articleDao;

    @Test
    public void testInsertArticle() {
        Article article = new Article();
        article.setName("黄斌龙1");
        article.setText("这个是文件内容 请注意");
        articleDao.save(article);
        Article article1 = new Article();
        article1.setName("huangbin龙3");
        article1.setText("这个是文件内容 file content  请注意");
        articleDao.save(article1);
    }

    @Test
    public void testUpdateArticle() {
        Optional<Article> articleOptional = articleDao.findById(Long.parseLong("775673602711224320"));
        articleOptional.ifPresent(article -> {
            article.setName("黄斌龙 test");
            article.setText("这个是文件内容 this is a content");
            articleDao.save(article);
        });
    }

    @Test
    public void testSearchArticle() {
        List<SearchHit<Article>> list = articleDao.findAllByNameLikeOrTextLike("haohao", "neirong");
        System.out.println(list.size());
    }


    @Test
    public void testAggregationArticle() {

        /**
         * 创建查询体
         */
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        /**
         * 设置聚合条件
         */
        RangeQueryBuilder query = QueryBuilders.rangeQuery("id").from("775687870974263296").to("775687872832339968");

        /**
         * 将聚合条件设置入查询体之中
         */
        builder.must(query);
        Iterable<Article> dataList = articleDao.search(builder);

        System.out.println();
    }


}

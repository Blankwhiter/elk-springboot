package com.elk.elktcp;

import com.elk.elktcp.dao.UserDao;
import com.elk.elktcp.entity.Log;
import com.elk.elktcp.entity.User;
import com.elk.elktcp.service.LogService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElkTcpApplicationTests {

    @Value("${LOGSTASH_HOST}")
    private String ip;

    private final static Logger log = LoggerFactory.getLogger("newtest");
    @Test
    public void test() {
        log.info("ip:{}的filebeat kafka logstash   测试 info 成功了！！！",ip);
        log.warn("ip:{}的filebeat kafka logstash   测试 warn 成功了！！！",ip);
        log.error("ip:{}的filebeat kafka logstash   测试 error 成功了！！",ip);
    }


    @Autowired
    @Qualifier("elasticsearchTemplate")
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private LogService logService;

    @Test
    public void testElasticSearchService(){
        List<SearchHit<Log>>list = logService.findByMethodNameLike("test");
        System.out.println(list.size());
        for (int i = 0; i < 10; i++) {
            Log log = list.get(i).getContent();
            List<String> methodName = list.get(i).getHighlightField("methodName");
            System.out.println(log.getBeanName() +" : " + log.getMethodName());
        }
    }


    @Test
    public void testElasticSearchTemplate(){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        NativeSearchQuery searchQuery = new NativeSearchQuery(boolQueryBuilder);
        SearchHits<Log> search = elasticsearchRestTemplate.search(searchQuery, Log.class);
        System.out.println(search.getTotalHits());
    }

    @Autowired
    private UserDao userDao;

    @Test
    public void testInsert(){
        User user = new User();
        user.setId(2L);
        user.setAge(30);
        user.setDesc("这个是描述，测试使用@，洛神，庭流，123123123123");
        user.setUserName("黄斌龙123");
        userDao.save(user);
    }
    @Test
    public void testSelect(){
//        List<SearchHit<User>> list = userDao.findByUserNameLike("黄");
//        System.out.println(list.size());
//
        List<SearchHit<User>> list = userDao.findByDescLike("庭流");
        System.out.println(list.size());


    }
}

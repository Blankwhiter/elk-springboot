# elk-springboot
1.elk-tcp模块在测试在src/test下进行测试



```
docker run -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -d -p 9200:9200 -p 9300:9300  -e "discovery.type=single-node" -v /home/software/elasticsearch/plugins:/usr/share/elasticsearch/plugins  -v /home/software/elasticsearch/data:/usr/share/elasticsearch/data -v /home/software/elasticsearch/config/es-single.yml:/usr/share/elasticsearch/config/elasticsearch.yml --name elasticsearch-single elasticsearch:7.9.3
```


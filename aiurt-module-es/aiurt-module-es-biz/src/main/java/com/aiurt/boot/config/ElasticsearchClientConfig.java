package com.aiurt.boot.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author
 * @Description: Elasticsearch客户端配置类
 */
@Configuration
public class ElasticsearchClientConfig {

    @Value("${spring.elasticsearch.uris}")
    private String uris;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // Create the HLRC
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.1.29", 9200, "http"))
        );
        return restHighLevelClient;
    }
}

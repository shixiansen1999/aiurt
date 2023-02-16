package com.aiurt.boot.config;

import cn.hutool.core.util.StrUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * @Author
 * @Description: Elasticsearch客户端配置类
 */
@Configuration
public class ElasticsearchClientConfig {

    @Value("${spring.elasticsearch.uris:http://127.0.0.1:9200}")
    private String uris;

    @Value("${spring.elasticsearch.connection-timeout}")
    private int connectTimeoutMillis;

    @Value("${spring.elasticsearch.socket-timeout}")
    private int socketTimeoutMillis;

    /**
     * @Value("${spring.elasticsearch.username}")
     */
    private String username;

    /**
     * @Value("${spring.elasticsearch.password}")
     */
    private String password;

    @Scope("singleton")
    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        List<String> host = StrUtil.split(uris, ',');
        HttpHost[] httpHosts = new HttpHost[host.size()];
        for (int i = 0; i < httpHosts.length; i++) {
            httpHosts[i] = HttpHost.create(host.get(i));
        }

        RestClientBuilder clientBuilder = RestClient.builder(httpHosts);
        clientBuilder.setRequestConfigCallback(
                builder -> {
                    builder.setConnectTimeout(connectTimeoutMillis);
                    builder.setSocketTimeout(socketTimeoutMillis);
                    return builder;
                }
        );

        if (StrUtil.isNotEmpty(username)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            clientBuilder.setHttpClientConfigCallback(
                    httpClientBuilder -> {
                        httpClientBuilder.disableAuthCaching();
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        return httpClientBuilder;
                    }
            );
        } else {
            clientBuilder.setHttpClientConfigCallback(
                    httpClientBuilder -> {
                        httpClientBuilder.disableAuthCaching();
                        return httpClientBuilder;
                    }
            );
        }

        // Create the HLRC
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(clientBuilder);
        return restHighLevelClient;
    }
}

package com.ascendcorp.example.springconnectionpoolmetricsexample.resttemplate;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DefaultRestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public HttpComponentsClientHttpRequestFactory defaultHttpRequestFactory(PoolingHttpClientConnectionManager defaultHttpClientConnectionManager) {
//        CloseableHttpClient closeableHttpClient = HttpClients.custom()
//                .setConnectionManager(defaultHttpClientConnectionManager)
//                .build();
//        return new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
//    }
//
//    @Bean
//    @Primary
//    public RestTemplate defaultRestTemplate(HttpComponentsClientHttpRequestFactory defaultHttpRequestFactory) {
//        return new RestTemplate(defaultHttpRequestFactory);
//    }
//
    @Bean
    public PoolingHttpClientConnectionManager defaultHttpClientConnectionManager() {
        return new PoolingHttpClientConnectionManager();
    }
}

package com.shop.order_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Creates a ready-to-use WebClient pointed at Product Service.
 * The base URL comes from application.properties (product.service.url), which
 * defaults to http://localhost:8080 locally but can be overridden by an
 * environment variable in the cloud.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productWebClient(WebClient.Builder builder,
                                      @Value("${product.service.url}") String productServiceUrl) {
        return builder
                .baseUrl(productServiceUrl)   // every call is relative to this URL
                .build();
    }
}

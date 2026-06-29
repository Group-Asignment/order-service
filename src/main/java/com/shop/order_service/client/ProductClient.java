package com.shop.order_service.client;

import com.shop.order_service.dto.ProductResponse;
import com.shop.order_service.exception.ProductNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Calls Product Service to fetch a product's details.
 * This is the bridge between the two microservices.
 */
@Component
public class ProductClient {

    private final WebClient productWebClient;

    // Spring injects the WebClient bean we configured in WebClientConfig.
    public ProductClient(WebClient productWebClient) {
        this.productWebClient = productWebClient;
    }

    /**
     * GET /products/{id} on Product Service.
     * Returns the product, or throws ProductNotFoundException if it returns 404.
     */
    public ProductResponse getProductById(Long productId) {
        return productWebClient.get()
                .uri("/products/{id}", productId)        // builds /products/5 etc.
                .retrieve()                               // send the request
                // if Product Service replies 404, throw our own clear exception
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new ProductNotFoundException(productId)))
                .bodyToMono(ProductResponse.class)        // parse the JSON into our DTO
                .block();                                 // wait for the response (blocking call)
    }
}

package com.shop.order_service.service;

import com.shop.order_service.client.ProductClient;
import com.shop.order_service.dto.OrderRequest;
import com.shop.order_service.dto.OrderResponse;
import com.shop.order_service.dto.ProductResponse;
import com.shop.order_service.event.OrderEvent;
import com.shop.order_service.exception.ProductNotFoundException;
import com.shop.order_service.messaging.OrderEventPublisher;
import com.shop.order_service.model.Order;
import com.shop.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the order workflow. The repository, Product Service client, and
 * the RabbitMQ publisher are all mocked, so these tests are fast and need no
 * database, no Product Service, and no RabbitMQ.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_fetchesProduct_calculatesTotal_saves_andPublishes() {
        // given: a request for 3 units of product 2
        OrderRequest request = new OrderRequest();
        request.setCustomerId(7L);
        request.setProductId(2L);
        request.setQuantity(3);

        // Product Service will return this product (price 4500)
        ProductResponse product = new ProductResponse();
        product.setProductId(2L);
        product.setName("Keyboard");
        product.setUnitPrice(4500.0);
        when(productClient.getProductById(2L)).thenReturn(product);

        // the repository returns the saved order with a generated id
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(1L);
            return o;
        });

        // when
        OrderResponse response = orderService.createOrder(request);

        // then: total is 4500 * 3 = 13500, product name copied, status set
        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Keyboard");
        assertThat(response.getTotalPrice()).isEqualTo(13500.0);
        assertThat(response.getStatus()).isEqualTo("CREATED");

        // and: the order was saved AND an event was published
        verify(orderRepository).save(any(Order.class));
        verify(orderEventPublisher).publish(any(OrderEvent.class));
    }

    @Test
    void createOrder_whenProductMissing_throws_andDoesNotSaveOrPublish() {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(7L);
        request.setProductId(99L);
        request.setQuantity(1);

        when(productClient.getProductById(99L)).thenThrow(new ProductNotFoundException(99L));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ProductNotFoundException.class);

        // nothing should be saved or published if the product doesn't exist
        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publish(any());
    }
}

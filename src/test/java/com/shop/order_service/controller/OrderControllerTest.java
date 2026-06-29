package com.shop.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.order_service.dto.OrderRequest;
import com.shop.order_service.dto.OrderResponse;
import com.shop.order_service.exception.ProductNotFoundException;
import com.shop.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests the HTTP layer of OrderController. @WebMvcTest loads only the web layer;
 * the OrderService is replaced with a mock, so no DB/Product Service/RabbitMQ needed.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createOrder_returns201() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(7L);
        request.setProductId(2L);
        request.setQuantity(3);

        OrderResponse response = new OrderResponse(
                1L, 7L, 2L, "Keyboard", 3, 13500.0, LocalDateTime.now(), "CREATED");
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalPrice").value(13500.0));
    }

    @Test
    void createOrder_withMissingFields_returns400() throws Exception {
        OrderRequest invalid = new OrderRequest();   // no customerId/productId/quantity

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_whenProductMissing_returns404() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(7L);
        request.setProductId(99L);
        request.setQuantity(1);

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}

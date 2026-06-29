package com.shop.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The shape of the data we receive BACK from Product Service when we ask for a
 * product. Product Service returns more fields (description, category, stock),
 * but we only need these three for creating an order.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) tells Jackson to silently ignore
 * any extra fields in the JSON instead of throwing an error.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {
    private Long productId;
    private String name;
    private Double unitPrice;
}

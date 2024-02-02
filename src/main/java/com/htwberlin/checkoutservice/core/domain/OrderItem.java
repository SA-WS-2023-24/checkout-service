package com.htwberlin.checkoutservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String productId;
    private String name;
    private String description;
    private String quantity;
    private String price;
}

package com.htwberlin.checkoutservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String basketId;
    private String fullName;
    private String address;
    private List<OrderItem> orderItems;
    private String totalCost;
    private String shippingCost;
}

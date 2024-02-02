package com.htwberlin.checkoutservice.port.mapper;

import com.paypal.orders.*;
import com.htwberlin.checkoutservice.core.domain.OrderRequest;

import java.util.List;

public class OrderRequestMapper {

    private static final String CURRENCY = "EUR";

    public static AmountWithBreakdown orderRequestToAmountWithBreakdown(OrderRequest orderRequest) {
        return new AmountWithBreakdown().currencyCode(CURRENCY)
                .amountBreakdown(new AmountBreakdown().itemTotal(new Money().currencyCode(CURRENCY).value(orderRequest.getTotalCost()))
                        .shipping(new Money().currencyCode(CURRENCY).value(orderRequest.getShippingCost())));
    }

    public static List<Item> orderRequestToItems(OrderRequest orderRequest) {
        return orderRequest
                .getOrderItems()
                .stream()
                .map((orderItem) ->
                    new Item()
                            .name(orderItem.getName())
                            .description(orderItem.getDescription())
                            .sku(orderItem.getProductId())
                            .quantity(orderItem.getQuantity())
                            .unitAmount(new Money().currencyCode(CURRENCY).value(orderItem.getPrice()))
                )
                .toList();
    }

    public static ShippingDetail orderRequestToShippingDetail(OrderRequest orderRequest) {
        return new ShippingDetail()
                .name(new Name().fullName(orderRequest.getFullName()))
                .addressPortable(new AddressPortable().addressLine1(orderRequest.getAddress()));
    }
}

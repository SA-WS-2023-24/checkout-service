package com.htwberlin.checkoutservice.port.producer.basket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderMessage {
    private String basketId;
}

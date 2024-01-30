package com.htwberlin.checkoutservice.core.domain;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder implements Serializable {
    private String status;
    private String payId;
    private String redirectUrl;

    public PaymentOrder(String status) {
        this.status = status;
    }
}
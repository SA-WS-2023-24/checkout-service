package com.htwberlin.checkoutservice.port.user.controller;

import com.htwberlin.checkoutservice.core.domain.CompletedOrder;
import com.htwberlin.checkoutservice.core.domain.PaymentOrder;
import com.htwberlin.checkoutservice.core.service.impl.PaypalService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/checkout")
@CrossOrigin
public class CheckoutController {
    private final PaypalService paypalService;

    public CheckoutController(PaypalService paypalService) {
        this.paypalService = paypalService;
    }

    @PostMapping(value = "/init")
    public PaymentOrder createPayment(@RequestParam BigDecimal sum) {
        return paypalService.createPayment(sum);
    }

    @PostMapping(value = "/capture")
    public CompletedOrder completePayment(@RequestParam String token) {
        return paypalService.completePayment(token);
    }

    @GetMapping(value = "/order/{id}")
    public String orderDetails(@PathVariable String id) {
        return paypalService.orderDetails(id);
    }
}

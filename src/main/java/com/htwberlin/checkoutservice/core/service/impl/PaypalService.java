package com.htwberlin.checkoutservice.core.service.impl;

import com.htwberlin.checkoutservice.core.domain.CompletedOrder;
import com.htwberlin.checkoutservice.core.domain.PaymentOrder;
import com.htwberlin.checkoutservice.core.service.interfaces.IOrdersApi;
import com.htwberlin.checkoutservice.core.service.interfaces.IProducer;
import com.htwberlin.checkoutservice.port.mapper.OrderRequestMapper;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class PaypalService {

    private final IProducer messageProducer;

    private final PayPalHttpClient payPalHttpClient;

    private final IOrdersApi ordersApi;

    public PaypalService(IProducer messageProducer, PayPalHttpClient payPalHttpClient, IOrdersApi ordersApi) {
        this.messageProducer = messageProducer;
        this.payPalHttpClient = payPalHttpClient;
        this.ordersApi = ordersApi;
    }

    public PaymentOrder createPayment(com.htwberlin.checkoutservice.core.domain.OrderRequest order) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().referenceId("PUHF")
                .description("Sporting Goods").customId("CUST-HighFashions").softDescriptor("HighFashions")
                .amountWithBreakdown(OrderRequestMapper.orderRequestToAmountWithBreakdown(order))
                .items(OrderRequestMapper.orderRequestToItems(order))
                .shippingDetail(OrderRequestMapper.orderRequestToShippingDetail(order));

        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("https://google.com")
                .cancelUrl("https://localhost:4200/cancel");

        orderRequest.applicationContext(applicationContext);

        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order orderResponse = orderHttpResponse.result();

            String redirectUrl = orderResponse.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();

            messageProducer.publishOrderSuccessfulEvent(order.getBasketId());
            return new PaymentOrder("success", orderResponse.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaymentOrder("Error");
        }
    }

    public String orderDetails(String orderId) {
        return ordersApi.getOrderDetails(orderId);
    }

    public CompletedOrder completePayment(String token) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
        try {
            HttpResponse<Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            if (httpResponse.result().status() != null) {
                String orderId = httpResponse.result().id();

                log.info(orderId);
                return new CompletedOrder("success", token, orderId);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return new CompletedOrder("error");
    }
}
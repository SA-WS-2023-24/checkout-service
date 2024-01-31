package com.htwberlin.checkoutservice.core.service.impl;

import com.htwberlin.checkoutservice.config.PaypalConfig;
import com.htwberlin.checkoutservice.core.domain.CompletedOrder;
import com.htwberlin.checkoutservice.core.domain.PaymentOrder;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class PaypalService {

    private final PayPalHttpClient payPalHttpClient;

    private final RestTemplate restTemplate;

    public PaypalService(PayPalHttpClient payPalHttpClient, RestTemplate restTemplate) {
        this.payPalHttpClient = payPalHttpClient;
        this.restTemplate = restTemplate;
    }

    // need shipping cost,
    public PaymentOrder createPayment(BigDecimal fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");


//        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown()
//                .currencyCode("EUR").value(fee.toString());


        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().referenceId("PUHF")
                .description("Sporting Goods").customId("CUST-HighFashions").softDescriptor("HighFashions")
                .amountWithBreakdown(new AmountWithBreakdown().currencyCode("USD").value("220.00")
                        .amountBreakdown(new AmountBreakdown().itemTotal(new Money().currencyCode("USD").value("180.00"))
                                .shipping(new Money().currencyCode("USD").value("20.00"))
                                .handling(new Money().currencyCode("USD").value("10.00"))
                                .taxTotal(new Money().currencyCode("USD").value("20.00"))
                                .shippingDiscount(new Money().currencyCode("USD").value("10.00"))))
                .items(new ArrayList<Item>() {
                    {
                        add(new Item().name("T-shirt").description("Green XL").sku("sku01")
                                .unitAmount(new Money().currencyCode("USD").value("90.00"))
                                .tax(new Money().currencyCode("USD").value("10.00")).quantity("1")
                                .category("PHYSICAL_GOODS"));
                        add(new Item().name("Shoes").description("Running, Size 10.5").sku("sku02")
                                .unitAmount(new Money().currencyCode("USD").value("45.00"))
                                .tax(new Money().currencyCode("USD").value("5.00")).quantity("2")
                                .category("PHYSICAL_GOODS"));
                    }
                })
                .shippingDetail(new ShippingDetail().name(new Name().fullName("John Doe"))
                        .addressPortable(new AddressPortable().addressLine1("123 Townsend St").addressLine2("Floor 6")
                                .adminArea2("San Francisco").adminArea1("CA").postalCode("94107").countryCode("US")));
        //.amountWithBreakdown(amountBreakdown);

        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl("https://google.com")
                .cancelUrl("https://localhost:4200/cancel");

        orderRequest.applicationContext(applicationContext);

        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            Order order = orderHttpResponse.result();

            String redirectUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();

            return new PaymentOrder("success", order.id(), redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new PaymentOrder("Error");
        }
    }

    public String orderDetails(String orderId) {
        String url = "https://api.sandbox.paypal.com/v2/checkout/orders/";
        String clientId = "AY3aeJqfgQiYDgR8QPK4CcUtkG6j26y6zvef3h90JWFZ_setjVnQrwaaqdtlkhNddDRj5ggVZH3ZgGMt";
        String clientSecret = "EMdUOvZgHvo6qmUEfO4HTNYlOlqa6mq4KuCSZZoZH9yQQ2pixVzYbSG6lyS7DUqDd97DXncKcRpxMBSv";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(clientId, clientSecret);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url + orderId,
                HttpMethod.GET,
                entity,
                String.class
        );
        return responseEntity.getBody();
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
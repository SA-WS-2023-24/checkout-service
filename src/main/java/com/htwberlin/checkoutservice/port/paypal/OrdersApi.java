package com.htwberlin.checkoutservice.port.paypal;

import com.htwberlin.checkoutservice.core.service.interfaces.IOrdersApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class OrdersApi implements IOrdersApi {

    private final RestTemplate restTemplate;

    @Value("${paypal.baseUrl}")
    private String url;

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.secret}")
    private String clientSecret;


    public OrdersApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public String getOrderDetails(String orderId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String orderEndpoint = "/v2/checkout/orders/";

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url + orderEndpoint + orderId,
                HttpMethod.GET,
                entity,
                String.class
        );

        return responseEntity.getBody();
    }
}

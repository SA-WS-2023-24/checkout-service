package com.htwberlin.checkoutservice.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class PaypalConfig {

    @Value("${paypal.baseUrl}")
    private String url;

    @Value("${paypal.clientId}")
    private String clientId;

    @Value("${paypal.secret}")
    private String clientSecret;

    @Bean
    public PayPalHttpClient getPaypalClient() {
        return new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
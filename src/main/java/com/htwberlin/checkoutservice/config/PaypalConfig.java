package com.htwberlin.checkoutservice.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PaypalConfig {
    @Bean
    public PayPalHttpClient getPaypalClient(
            @Value("${paypal.clientId}") String clientId,
            @Value("${paypal.secret}") String clientSecret) {
        return new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
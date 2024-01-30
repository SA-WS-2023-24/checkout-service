package com.htwberlin.checkoutservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String CREATE_CHECKOUT_QUEUE = "create_checkout";

    public static final String CHECKOUT_EXCHANGE = "checkout_exchange";


    @Bean
    public Queue createCheckoutQueue() {
        return new Queue(CREATE_CHECKOUT_QUEUE,false);
    }

    @Bean
    public TopicExchange checkoutTopicExchange() {
        return new TopicExchange(CHECKOUT_EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding bindCreateCheckoutQueue(TopicExchange exchange) {
        return BindingBuilder.bind(createCheckoutQueue()).to(exchange).with("checkout.create");
    }

    @Bean
    public RabbitTemplate productTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

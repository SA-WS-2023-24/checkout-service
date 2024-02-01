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
    public static final String REMOVE_BASKET_QUEUE= "delete_basket";

    public static final String BASKET_EXCHANGE = "basket_exchange";


    @Bean
    public Queue deleteBasketQueue() {
        return new Queue(REMOVE_BASKET_QUEUE,false);
    }

    @Bean
    public TopicExchange basketTopicExchange() {
        return new TopicExchange(BASKET_EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding bindCreateCheckoutQueue(TopicExchange exchange) {
        return BindingBuilder.bind(deleteBasketQueue()).to(exchange).with("basket.delete");
    }

    @Bean
    public RabbitTemplate orderTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

package com.htwberlin.checkoutservice.port.producer.basket;

import com.htwberlin.checkoutservice.core.service.interfaces.IProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderMessageProducer implements IProducer {

    private final RabbitTemplate orderTemplate;

    public OrderMessageProducer(RabbitTemplate orderTemplate) {
        this.orderTemplate = orderTemplate;
    }

    @Override
    public void publishOrderSuccessfulEvent(String basketId) {
        OrderMessage message = new OrderMessage(basketId);
        log.info(String.format("BASKET DELETE -> %s", message));
        orderTemplate.convertAndSend(message);
    }
}

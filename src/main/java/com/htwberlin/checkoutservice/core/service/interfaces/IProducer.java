package com.htwberlin.checkoutservice.core.service.interfaces;

public interface IProducer {
    void publishOrderSuccessfulEvent(String basketId);
}

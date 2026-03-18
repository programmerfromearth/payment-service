package com.iprody.common.payment.app.async;

public interface MessageHandler<T extends Message> {

    void handle(T message);
}

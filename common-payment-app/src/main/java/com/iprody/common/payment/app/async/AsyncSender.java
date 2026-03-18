package com.iprody.common.payment.app.async;

public interface AsyncSender<T extends Message> {

    void send(T message);
}

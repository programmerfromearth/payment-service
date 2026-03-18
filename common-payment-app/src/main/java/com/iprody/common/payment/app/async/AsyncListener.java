package com.iprody.common.payment.app.async;

public interface AsyncListener<T extends Message> {

    void onMessage(T message);
}

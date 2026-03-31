package com.iprody.xpayment.adapter.app.checkstate.impl;

import com.iprody.xpayment.adapter.app.checkstate.api.PaymentStatusCheckHandler;

import java.util.UUID;

public class PaymentStatusCheckHandlerImpl implements PaymentStatusCheckHandler {
    @Override
    public boolean handle(UUID paymentGuid) {
        return true;//TODO implement logic
    }
}

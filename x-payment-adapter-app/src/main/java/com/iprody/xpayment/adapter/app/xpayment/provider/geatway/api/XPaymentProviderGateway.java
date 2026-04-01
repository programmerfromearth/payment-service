package com.iprody.xpayment.adapter.app.xpayment.provider.geatway.api;

import com.iprody.xpayment.adapter.app.async.model.ChargeResponseDto;
import com.iprody.xpayment.adapter.app.async.model.CreateChargeRequestDto;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

public interface XPaymentProviderGateway {

    ChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequestDto) throws RestClientException;

    ChargeResponseDto retrieveCharge(UUID id) throws RestClientException;
}

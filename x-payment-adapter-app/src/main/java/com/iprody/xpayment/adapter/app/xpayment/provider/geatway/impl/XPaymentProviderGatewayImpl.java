package com.iprody.xpayment.adapter.app.xpayment.provider.geatway.impl;

import com.iprody.xpayment.adapter.app.async.model.ChargeResponseDto;
import com.iprody.xpayment.adapter.app.async.model.CreateChargeRequestDto;
import com.iprody.xpayment.adapter.app.mapper.XPaymentProviderMapper;
import com.iprody.xpayment.adapter.app.xpayment.provider.geatway.api.XPaymentProviderGateway;
import com.iprody.xpayment.api.client.DefaultApi;
import com.iprody.xpayment.api.model.ChargeResponse;
import com.iprody.xpayment.api.model.CreateChargeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {
    private final DefaultApi defaultApi;
    private final XPaymentProviderMapper xPaymentProviderMapper;

    @Override
    public ChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequestDto) throws RestClientException {
        try {
            final CreateChargeRequest createChargeRequest =
                    xPaymentProviderMapper.mapToCreateChargeRequest(createChargeRequestDto);
            final ChargeResponse chargeResponse = defaultApi.createCharge(createChargeRequest);

            return xPaymentProviderMapper.mapToChargeResponseDto(chargeResponse);
        } catch (RestClientException e) {
            throw toRestClientException("POST /charges failed", e);
        }
    }

    @Override
    public ChargeResponseDto retrieveCharge(UUID id) throws RestClientException {
        try {
            final ChargeResponse chargeResponse = defaultApi.retrieveCharge(id);

            return xPaymentProviderMapper.mapToChargeResponseDto(chargeResponse);
        } catch (RestClientException e) {
            throw toRestClientException("GET /charges/{id} failed (id=%s)".formatted(id), e);
        }
    }

    private RestClientException toRestClientException(String prefix, RestClientException e) {
        return new RestClientException(prefix, e);
    }
}

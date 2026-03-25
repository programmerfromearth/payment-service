package com.iprody.xpayment.adapter.app.api;

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

    @Override
    public ChargeResponse createCharge(CreateChargeRequest createChargeRequest) throws RestClientException {
        try {
            return defaultApi.createCharge(createChargeRequest);
        } catch (RestClientException e) {
            throw toRestClientException("POST /charges failed", e);
        }
    }

    @Override
    public ChargeResponse retrieveCharge(UUID id) throws RestClientException {
        try {
            return defaultApi.retrieveCharge(id);
        } catch (RestClientException e) {
            throw toRestClientException("GET /charges/{id} failed (id=" + id + ")", e);
        }
    }

    private RestClientException toRestClientException(String prefix, RestClientException e) {
        final String msg = String.format(
                "%s: HTTP %d, body: %s", prefix, e.getCode(), safeStringConverter(e.getResponseBody()));
        return new RestClientException(msg, e);
    }

    private String safeStringConverter(String s) {
        return s == null ? "<empty>" : s;
    }
}

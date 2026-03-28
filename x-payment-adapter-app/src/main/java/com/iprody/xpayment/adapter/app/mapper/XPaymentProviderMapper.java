package com.iprody.xpayment.adapter.app.mapper;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterResponseMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterStatus;
import com.iprody.common.payment.app.time.api.TimeProvider;
import com.iprody.xpayment.adapter.app.async.model.ChargeResponseDto;
import com.iprody.xpayment.adapter.app.async.model.CreateChargeRequestDto;
import com.iprody.xpayment.api.model.ChargeResponse;
import com.iprody.xpayment.api.model.CreateChargeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class XPaymentProviderMapper {

    @Autowired
    protected TimeProvider timeProvider;

    public abstract CreateChargeRequest mapToCreateChargeRequest(CreateChargeRequestDto createChargeRequest);

    public abstract ChargeResponseDto mapToChargeResponseDto(ChargeResponse chargeResponse);

    @Mapping(target = "receiptEmail", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(source = "paymentGuid", target = "order")
    public abstract CreateChargeRequestDto toCreateChargeRequestDto(XPaymentAdapterRequestMessage model);

    @Mapping(target = "messageGuid", ignore = true)
    @Mapping(source = "order", target = "paymentGuid")
    @Mapping(source = "id", target = "transactionRefId")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapToStatus")
    @Mapping(target = "occurredAt", expression = "java(timeProvider.now())")
    public abstract XPaymentAdapterResponseMessage toXPaymentAdapterResponseMessage(ChargeResponseDto model);

    @Mapping(target = "transactionRefId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "messageGuid", ignore = true)
    @Mapping(target = "occurredAt", expression = "java(timeProvider.now())")
    public abstract XPaymentAdapterResponseMessage toXPaymentAdapterResponseMessage(XPaymentAdapterRequestMessage model);

    @Named("mapToStatus")
    XPaymentAdapterStatus mapToStatus(String status) {
        return XPaymentAdapterStatus.valueOf(status);
    }
}

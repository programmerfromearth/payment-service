package com.iprody.payment.service.app.mapper;

import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface XPaymentAdapterMapper {

    @Mapping(source = "guid", target = "paymentGuid")
    @Mapping(source = "updatedAt", target = "occurredAt")
    XPaymentAdapterRequestMessage toXPaymentAdapterRequestMessage(PaymentEntity entity);
}

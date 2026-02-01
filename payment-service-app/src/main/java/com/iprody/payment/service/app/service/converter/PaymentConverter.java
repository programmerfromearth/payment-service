package com.iprody.payment.service.app.service.converter;

import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.service.payment.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentConverter {

    Payment toModel(PaymentEntity entity);
}

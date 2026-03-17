package com.iprody.payment.service.app.async;

import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler<XPaymentAdapterResponseMessage> {

    private final PaymentService paymentService;

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        log.info("Handle message: {}", message);
        final UUID paymentGuid = message.getPaymentGuid();

        final PaymentDto existingPaymentDto = paymentService.getById(paymentGuid);
        final PaymentDto paymentToUpdate = convertToPaymentToUpdate(existingPaymentDto, message);

        paymentService.update(paymentGuid, paymentToUpdate);
    }

    private PaymentDto convertToPaymentToUpdate(PaymentDto paymentDto, XPaymentAdapterResponseMessage message) {
        final PaymentStatus paymentStatus = convertToPaymentStatus(message.getStatus());

        return PaymentDto.builder()
                .inquiryRefId(paymentDto.inquiryRefId())
                .amount(message.getAmount())
                .currency(message.getCurrency())
                .transactionRefId(message.getTransactionRefId())
                .status(paymentStatus)
                .note(paymentDto.note())
                .createdAt(paymentDto.createdAt())
                .updatedAt(message.getOccurredAt())
                .build();
    }

    private PaymentStatus convertToPaymentStatus(XPaymentAdapterStatus adapterStatus) {
        return switch (adapterStatus) {
            case PROCESSING -> PaymentStatus.PENDING;
            case CANCELED -> PaymentStatus.DECLINED;
            case SUCCEEDED -> PaymentStatus.APPROVED;
        };
    }
}

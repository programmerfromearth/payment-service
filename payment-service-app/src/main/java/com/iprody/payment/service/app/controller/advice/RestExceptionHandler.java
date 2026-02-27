package com.iprody.payment.service.app.controller.advice;

import com.iprody.payment.service.app.common.api.TimeProvider;
import com.iprody.payment.service.app.controller.advice.model.ErrorResponse;
import com.iprody.payment.service.app.controller.advice.model.PaymentErrorResponse;
import com.iprody.payment.service.app.exception.PaymentEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {
    private final TimeProvider timeProvider;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PaymentEntityNotFoundException.class)
    protected PaymentErrorResponse handlePaymentEntityNotFoundException(
            PaymentEntityNotFoundException ex) {

        return PaymentErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(timeProvider.now().toInstant())
                .operation(ex.getOperation())
                .entityId(ex.getEntityId())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ErrorResponse handleException(Exception ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(timeProvider.now().toInstant())
                .build();
    }
}

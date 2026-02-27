package com.iprody.payment.service.app.controller.advice;

import com.iprody.payment.service.app.controller.advice.model.ErrorResponse;
import com.iprody.payment.service.app.controller.advice.model.PaymentErrorResponse;
import com.iprody.payment.service.app.exception.PaymentEntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PaymentEntityNotFoundException.class)
    protected ResponseEntity<PaymentErrorResponse> handlePaymentEntityNotFoundException(
            PaymentEntityNotFoundException ex) {

        final PaymentErrorResponse response = PaymentErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .operation(ex.getOperation())
                .entityId(ex.getEntityId())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
        final ErrorResponse response = ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

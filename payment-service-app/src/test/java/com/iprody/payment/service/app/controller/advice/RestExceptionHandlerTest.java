package com.iprody.payment.service.app.controller.advice;

import com.iprody.payment.service.app.controller.advice.model.ErrorResponse;
import com.iprody.payment.service.app.controller.advice.model.PaymentErrorResponse;
import com.iprody.payment.service.app.exception.PaymentEntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.iprody.payment.service.app.util.CommonConstants.NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE;
import static com.iprody.payment.service.app.util.CommonConstants.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {

    @InjectMocks
    private RestExceptionHandler handler;

    @Test
    void handlePaymentEntityNotFoundException_ShouldBuildCorrectResponse() {
        // given
        final UUID entityId = UUID.randomUUID();
        final PaymentEntityNotFoundException ex = new PaymentEntityNotFoundException(entityId, UPDATE);

        // when
        final ResponseEntity<PaymentErrorResponse> response = handler.handlePaymentEntityNotFoundException(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo(
                String.format(NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE, entityId));
        assertThat(response.getBody().entityId()).isEqualTo(entityId);
        assertThat(response.getBody().operation()).isEqualTo(UPDATE);
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        // given
        final String exceptionMessage = "Something went wrong";
        final Exception ex = new RuntimeException(exceptionMessage);

        // when
        final ResponseEntity<ErrorResponse> response = handler.handleException(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo(exceptionMessage);
        assertThat(response.getBody().timestamp()).isNotNull();
    }
}
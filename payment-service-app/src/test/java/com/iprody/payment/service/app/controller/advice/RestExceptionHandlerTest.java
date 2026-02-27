package com.iprody.payment.service.app.controller.advice;

import com.iprody.payment.service.app.common.api.TimeProvider;
import com.iprody.payment.service.app.controller.advice.model.ErrorResponse;
import com.iprody.payment.service.app.controller.advice.model.PaymentErrorResponse;
import com.iprody.payment.service.app.exception.PaymentEntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.iprody.payment.service.app.util.CommonConstants.UPDATE;
import static com.iprody.payment.service.app.util.TestConstants.OFFSET_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private RestExceptionHandler handler;

    @Test
    void handlePaymentEntityNotFoundException_ShouldBuildCorrectResponse() {
        // given
        final UUID entityId = UUID.randomUUID();
        final PaymentEntityNotFoundException ex = new PaymentEntityNotFoundException(entityId, UPDATE);

        when(timeProvider.now()).thenReturn(OFFSET_DATE_TIME);

        // when
        final PaymentErrorResponse response = handler.handlePaymentEntityNotFoundException(ex);

        // then
        assertThat(response).isNotNull();
        verify(timeProvider).now();
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        // given
        final String exceptionMessage = "Something went wrong";
        final Exception ex = new RuntimeException(exceptionMessage);

        when(timeProvider.now()).thenReturn(OFFSET_DATE_TIME);

        // when
        final ErrorResponse response = handler.handleException(ex);

        // then
        assertThat(response).isNotNull();
        verify(timeProvider).now();
    }
}
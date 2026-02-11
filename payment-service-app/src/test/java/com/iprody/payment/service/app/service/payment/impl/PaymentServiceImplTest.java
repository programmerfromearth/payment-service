package com.iprody.payment.service.app.service.payment.impl;

import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import com.iprody.payment.service.app.converter.PaymentConverter;
import com.iprody.payment.service.app.service.payment.model.Payment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentConverter paymentConverter;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void whenPaymentsExistThenReturnMappedList() {
        final PaymentEntity entity = new PaymentEntity();
        final Payment model = Payment.builder().guid(UUID.randomUUID()).build();

        when(paymentRepository.findAll()).thenReturn(List.of(entity));
        when(paymentConverter.toModel(entity)).thenReturn(model);

        final List<Payment> result = paymentService.getAllPayments();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(model);
        verify(paymentRepository).findAll();
        verify(paymentConverter).toModel(any());
    }

    @Test
    void whenIdExistsThenReturnMappedPayment() {
        final UUID id = UUID.randomUUID();

        final PaymentEntity entity = new PaymentEntity();
        final Payment model = Payment.builder().guid(id).build();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(paymentConverter.toModel(entity)).thenReturn(model);

        final Optional<Payment> result = paymentService.findPaymentById(id);

        assertThat(result).isPresent().contains(model);
    }

    @Test
    void whenIdNotExistsThenReturnEmpty() {
        final UUID id = UUID.randomUUID();

        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        final Optional<Payment> result = paymentService.findPaymentById(id);

        assertThat(result).isEmpty();
        verifyNoInteractions(paymentConverter);
    }
}
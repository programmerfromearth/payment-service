package com.iprody.payment.service.app.service.payment.impl;

import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.quality.Strictness.STRICT_STUBS;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = STRICT_STUBS)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void getAllPaymentsTest() {
        final PaymentEntity entity = new PaymentEntity();
        final PaymentDto model = PaymentDto.builder()
                .guid(UUID.randomUUID())
                .build();

        when(paymentRepository.findAll()).thenReturn(List.of(entity));
        when(paymentMapper.toDto(entity)).thenReturn(model);

        paymentService.getAllPayments();

        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findAll();
        inOrder.verify(paymentMapper).toDto(entity);
    }

    @Test
    void findPaymentByIdTest() {
        final UUID id = UUID.randomUUID();

        final PaymentEntity entity = new PaymentEntity();
        final PaymentDto model = PaymentDto.builder().build();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(paymentMapper.toDto(entity)).thenReturn(model);

        paymentService.findPaymentById(id);

        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(id);
        inOrder.verify(paymentMapper).toDto(entity);
    }

    @Test
    void findPaymentByIdWhenPaymentsNotExist() {
        final UUID id = UUID.randomUUID();

        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        paymentService.findPaymentById(id);

        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(id);
        inOrder.verify(paymentMapper, never()).toDto(any(PaymentEntity.class));
    }

    @Test
    void searchPagedByFilterTest() {
        final PaymentFilter paymentFilter = PaymentFilter.builder().build();
        final Pageable pageable = Pageable.unpaged();

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        paymentService.searchPagedByFilter(paymentFilter, pageable);

        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
        inOrder.verify(paymentMapper, never()).toDto(any(PaymentEntity.class));
    }
}
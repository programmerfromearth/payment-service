package com.iprody.payment.service.app.service.payment.impl;

import com.iprody.payment.service.app.common.api.TimeProvider;
import com.iprody.payment.service.app.controller.payment.model.PaymentToPartUpdateRequest;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.iprody.payment.service.app.util.CommonConstants.NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE;
import static com.iprody.payment.service.app.util.TestConstants.OFFSET_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.STRICT_STUBS;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = STRICT_STUBS)
class PaymentServiceImplTest {

    @Captor
    ArgumentCaptor<PaymentEntity> paymentEntityCaptor;

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void getAllPaymentsTest() {
        // given
        final PaymentEntity entity = new PaymentEntity();
        final PaymentDto model = PaymentDto.builder()
                .guid(UUID.randomUUID())
                .build();

        when(paymentRepository.findAll()).thenReturn(List.of(entity));
        when(paymentMapper.toDto(entity)).thenReturn(model);

        // when
        paymentService.getAllPayments();

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findAll();
        inOrder.verify(paymentMapper).toDto(entity);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void findPaymentByIdTest() {
        // given
        final UUID id = UUID.randomUUID();

        final PaymentEntity entity = new PaymentEntity();
        final PaymentDto model = PaymentDto.builder().build();

        when(paymentRepository.findById(id)).thenReturn(Optional.of(entity));
        when(paymentMapper.toDto(entity)).thenReturn(model);

        // when
        paymentService.findPaymentById(id);

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(id);
        inOrder.verify(paymentMapper).toDto(entity);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void findPaymentByIdWhenPaymentsNotExist() {
        // given
        final UUID id = UUID.randomUUID();

        when(paymentRepository.findById(id)).thenReturn(Optional.empty());

        // when
        paymentService.findPaymentById(id);

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(id);
        inOrder.verify(paymentMapper, never()).toDto(any(PaymentEntity.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void searchPagedByFilterTest() {
        // given
        final PaymentFilter paymentFilter = PaymentFilter.builder().build();
        final Pageable pageable = Pageable.unpaged();

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        // when
        paymentService.searchPagedByFilter(paymentFilter, pageable);

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
        inOrder.verify(paymentMapper, never()).toDto(any(PaymentEntity.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void whenThereIsNoPaymentWithIdThenGetByIdThrowException() {
        // given
        final UUID guid = UUID.randomUUID();

        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        // when & Then
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.getById(guid)
        );
        assertEquals(NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE.formatted(guid), exception.getMessage());
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verify(paymentMapper, never()).toDto(any(PaymentEntity.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void getByIdTest() {
        // given
        final UUID guid = UUID.randomUUID();

        final PaymentEntity entity = new PaymentEntity();
        final PaymentDto dto = PaymentDto.builder().build();

        when(paymentRepository.findById(guid)).thenReturn(Optional.of(entity));
        when(paymentMapper.toDto(entity)).thenReturn(dto);

        // when
        paymentService.getById(guid);

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verify(paymentMapper).toDto(entity);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void createTest() {
        // given
        final PaymentDto paymentToCreate = PaymentDto.builder().build();
        final PaymentEntity entityToSave = new PaymentEntity();
        final PaymentEntity savedEntity = new PaymentEntity();

        when(timeProvider.now()).thenReturn(OFFSET_DATE_TIME);
        when(paymentMapper.toEntity(paymentToCreate)).thenReturn(entityToSave);
        when(paymentRepository.save(entityToSave)).thenReturn(savedEntity);

        // when
        paymentService.create(paymentToCreate);

        // then
        final InOrder inOrder = inOrder(timeProvider, paymentMapper, paymentRepository);
        inOrder.verify(timeProvider).now();
        inOrder.verify(paymentMapper).toEntity(paymentToCreate);
        inOrder.verify(paymentRepository).save(paymentEntityCaptor.capture());
        inOrder.verify(paymentMapper).toDto(savedEntity);
        inOrder.verifyNoMoreInteractions();

        final OffsetDateTime createdAt = paymentEntityCaptor.getValue().getCreatedAt();
        final OffsetDateTime updatedAt = paymentEntityCaptor.getValue().getUpdatedAt();
        assertEquals(createdAt, updatedAt);
    }

    @Test
    void whenThereIsNoPaymentWithIdThenUpdateThrowException() {
        // given
        final UUID guid = UUID.randomUUID();
        final PaymentDto paymentToUpdate = PaymentDto.builder().build();
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        // when & Then
        final EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.update(guid, paymentToUpdate)
        );
        assertEquals(String.format(NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE, guid), exception.getMessage());
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verify(paymentMapper, never()).toDto(any(PaymentEntity.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void updateTest() {
        // given
        final UUID guid = UUID.randomUUID();
        final PaymentDto paymentToUpdate = PaymentDto.builder()
                .guid(UUID.randomUUID())
                .inquiryRefId(UUID.randomUUID())
                .amount(BigDecimal.ONE)
                .currency("USD")
                .transactionRefId(UUID.randomUUID())
                .status(PaymentStatus.PENDING)
                .note("note")
                .build();

        final PaymentEntity entityToUpdate = new PaymentEntity();
        when(timeProvider.now()).thenReturn(OFFSET_DATE_TIME);
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(entityToUpdate));

        // when
        paymentService.update(guid, paymentToUpdate);

        // then
        final InOrder inOrder = inOrder(timeProvider, paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verify(timeProvider).now();
        inOrder.verify(paymentMapper).toDto(entityToUpdate);

        assertThat(entityToUpdate.getGuid()).isNull();
        assertThat(entityToUpdate.getInquiryRefId()).isEqualTo(paymentToUpdate.inquiryRefId());
        assertThat(entityToUpdate.getAmount()).isEqualTo(paymentToUpdate.amount());
        assertThat(entityToUpdate.getCurrency()).isEqualTo(paymentToUpdate.currency());
        assertThat(entityToUpdate.getTransactionRefId()).isEqualTo(paymentToUpdate.transactionRefId());
        assertThat(entityToUpdate.getStatus()).isEqualTo(paymentToUpdate.status());
        assertThat(entityToUpdate.getNote()).isEqualTo(paymentToUpdate.note());
        assertThat(entityToUpdate.getCreatedAt()).isNull();
        assertThat(entityToUpdate.getUpdatedAt()).isNotNull();

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void whenThereIsNoPaymentWithIdThenUpdateNoteThrowException() {
        // given
        final UUID guid = UUID.randomUUID();
        final PaymentToPartUpdateRequest toPartUpdateRequest = PaymentToPartUpdateRequest.builder().build();
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        // when & Then
        final EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.updateNote(guid, toPartUpdateRequest)
        );
        assertEquals(String.format(NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE, guid), exception.getMessage());
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verify(paymentMapper, never()).toDto(any(PaymentEntity.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void updateNoteTest() {
        // given
        final UUID guid = UUID.randomUUID();
        final PaymentToPartUpdateRequest toPartUpdateRequest = PaymentToPartUpdateRequest.builder()
                .note("note")
                .build();

        final PaymentEntity entityToUpdate = new PaymentEntity();
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(entityToUpdate));

        // when
        paymentService.updateNote(guid, toPartUpdateRequest);

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verify(paymentMapper).toDto(entityToUpdate);
        assertThat(entityToUpdate.getNote()).isEqualTo(toPartUpdateRequest.note());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void whenThereIsNoPaymentWithIdDeleteThrowException() {
        // given
        final UUID guid = UUID.randomUUID();
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        // when & Then
        final EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.deleteById(guid)
        );
        assertEquals(String.format(NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE, guid), exception.getMessage());
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void deleteTest() {
        // given
        final UUID guid = UUID.randomUUID();

        final PaymentEntity entityToDelete = new PaymentEntity();
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(entityToDelete));
        doNothing().when(paymentRepository).delete(entityToDelete);

        // when
        paymentService.deleteById(guid);

        // then
        final InOrder inOrder = inOrder(paymentMapper, paymentRepository);
        inOrder.verify(paymentRepository).findById(guid);
        inOrder.verify(paymentRepository).delete(entityToDelete);
        inOrder.verifyNoMoreInteractions();
    }
}

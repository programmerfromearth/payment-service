package com.iprody.payment.service.app.service.payment.impl;

import com.iprody.payment.service.app.common.api.TimeProvider;
import com.iprody.payment.service.app.controller.payment.model.PaymentToPartUpdateRequest;
import com.iprody.payment.service.app.exception.PaymentEntityNotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import com.iprody.payment.service.app.persistency.specification.PaymentFilterFactory;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.iprody.payment.service.app.util.CommonConstants.DELETE;
import static com.iprody.payment.service.app.util.CommonConstants.GET;
import static com.iprody.payment.service.app.util.CommonConstants.PART_UPDATE;
import static com.iprody.payment.service.app.util.CommonConstants.UPDATE;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TimeProvider timeProvider;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public Optional<PaymentDto> findPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto);
    }

    @Override
    public PaymentDto getById(UUID id) {
        return findPaymentById(id)
                .orElseThrow(() -> new PaymentEntityNotFoundException(id, GET));
    }

    @Override
    public Page<PaymentDto> searchPagedByFilter(PaymentFilter paymentFilter, Pageable pageable) {
        final Specification<PaymentEntity> specification = PaymentFilterFactory.fromFilter(paymentFilter);

        return paymentRepository.findAll(specification, pageable)
                .map(paymentMapper::toDto);
    }

    @Transactional
    @Override
    public PaymentDto create(PaymentDto paymentToCreate) {
        final OffsetDateTime now = timeProvider.now();

        final PaymentEntity toCreate = paymentMapper.toEntity(paymentToCreate);
        toCreate.setCreatedAt(now);
        toCreate.setUpdatedAt(now);

        final PaymentEntity created = paymentRepository.save(toCreate);

        return paymentMapper.toDto(created);
    }

    @Transactional
    @Override
    public PaymentDto update(UUID id, PaymentDto paymentToUpdate) {
        final PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentEntityNotFoundException(id, UPDATE));

        updateEntity(paymentToUpdate, entity);

        return paymentMapper.toDto(entity);
    }

    @Transactional
    @Override
    public PaymentDto updateNote(UUID id, PaymentToPartUpdateRequest toPartUpdateRequest) {
        final PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentEntityNotFoundException(id, PART_UPDATE));

        updatePart(toPartUpdateRequest, entity);

        return paymentMapper.toDto(entity);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        paymentRepository.findById(id)
                .ifPresentOrElse(paymentRepository::delete, () -> {
                    throw new PaymentEntityNotFoundException(id, DELETE);
                });
    }

    private void updateEntity(PaymentDto paymentToUpdate, PaymentEntity entity) {
        final OffsetDateTime now = timeProvider.now();

        entity.setInquiryRefId(paymentToUpdate.inquiryRefId());
        entity.setAmount(paymentToUpdate.amount());
        entity.setCurrency(paymentToUpdate.currency());
        entity.setTransactionRefId(paymentToUpdate.transactionRefId());
        entity.setStatus(paymentToUpdate.status());
        entity.setNote(paymentToUpdate.note());
        entity.setUpdatedAt(now);
    }

    private static void updatePart(PaymentToPartUpdateRequest toPartUpdateRequest, PaymentEntity entity) {
        entity.setNote(toPartUpdateRequest.note());
    }
}

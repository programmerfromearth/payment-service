package com.iprody.payment.service.app.service.payment.impl;

import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.persistency.specification.PaymentFilterFactory;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

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
    public Page<PaymentDto> searchPagedByFilter(PaymentFilter paymentFilter, Pageable pageable) {
        final Specification<PaymentEntity> specification = PaymentFilterFactory.fromFilter(paymentFilter);

        return paymentRepository.findAll(specification, pageable)
                .map(paymentMapper::toDto);
    }
}

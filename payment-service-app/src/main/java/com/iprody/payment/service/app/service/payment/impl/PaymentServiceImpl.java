package com.iprody.payment.service.app.service.payment.impl;

import com.iprody.payment.service.app.converter.PaymentConverter;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.Payment;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import com.iprody.payment.service.app.service.payment.util.PaymentFilterFactory;
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
    private final PaymentConverter paymentConverter;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(paymentConverter::toModel)
                .toList();
    }

    @Override
    public Optional<Payment> findPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentConverter::toModel);
    }

    @Override
    public Page<Payment> searchPagedByFilter(PaymentFilter paymentFilter, Pageable pageable) {
        final Specification<PaymentEntity> specification = PaymentFilterFactory.fromFilter(paymentFilter);

        return paymentRepository.findAll(specification, pageable)
                .map(paymentConverter::toModel);
    }
}

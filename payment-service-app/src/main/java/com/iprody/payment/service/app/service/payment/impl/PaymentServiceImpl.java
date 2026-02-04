package com.iprody.payment.service.app.service.payment.impl;

import com.iprody.payment.service.app.persistency.repository.PaymentRepository;
import com.iprody.payment.service.app.service.converter.PaymentConverter;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.Payment;
import lombok.RequiredArgsConstructor;
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
}

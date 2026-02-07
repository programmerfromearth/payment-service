package com.iprody.payment.service.app.service.payment.api;

import com.iprody.payment.service.app.service.payment.model.Payment;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    List<Payment> getAllPayments();

    Optional<Payment> findPaymentById(UUID id);

    List<Payment> searchByFilter(PaymentFilter paymentFilter);
}

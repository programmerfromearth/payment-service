package com.iprody.payment.service.app.service.payment.api;

import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    List<PaymentDto> getAllPayments();

    Optional<PaymentDto> findPaymentById(UUID id);

    Page<PaymentDto> searchPagedByFilter(PaymentFilter paymentFilter, Pageable pageable);
}

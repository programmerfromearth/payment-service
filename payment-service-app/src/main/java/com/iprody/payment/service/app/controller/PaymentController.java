package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.model.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final List<Payment> PAYMENTS = List.of(
            new Payment(1L, 10),
            new Payment(2L, 20),
            new Payment(3L, 30));

    private final Map<Long, Payment> paymentByIdMap;

    public PaymentController() {
        paymentByIdMap = PAYMENTS.stream()
                .collect(toMap(Payment::getId, Function.identity()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("id") long id) {
        final Optional<Payment> result = Optional.ofNullable(paymentByIdMap.get(id));
        return ResponseEntity.of(result);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(PAYMENTS);
    }
}

package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.Payment;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("id") UUID id) {
        final Optional<Payment> result = paymentService.findPaymentById(id);
        return ResponseEntity.of(result);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        final List<Payment> result = paymentService.getAllPayments();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Payment>> searchByFilter(@ModelAttribute PaymentFilter paymentFilter) {
        final List<Payment> result = paymentService.searchByFilter(paymentFilter);
        return ResponseEntity.ok(result);
    }
}

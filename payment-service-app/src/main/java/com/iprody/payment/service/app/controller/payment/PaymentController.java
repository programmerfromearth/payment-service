package com.iprody.payment.service.app.controller.payment;

import com.iprody.payment.service.app.controller.payment.model.PaymentFilterRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentResponse;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final PaymentMapper paymentMapper;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("id") UUID id) {
        final Optional<PaymentResponse> result = paymentService.findPaymentById(id)
                .map(paymentMapper::toApiResponse);

        return ResponseEntity.of(result);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        final List<PaymentResponse> result = paymentService.getAllPayments().stream()
                .map(paymentMapper::toApiResponse)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PaymentResponse>> searchPagedByFilter(
            @ModelAttribute PaymentFilterRequest paymentFilterRequest,
            @PageableDefault(page = 0, size = 25) Pageable pageable) {
        final PaymentFilter paymentFilter = paymentMapper.toPaymentFilter(paymentFilterRequest);
        final Page<PaymentResponse> result = paymentService.searchPagedByFilter(paymentFilter, pageable)
                .map(paymentMapper::toApiResponse);

        return ResponseEntity.ok(result);
    }
}

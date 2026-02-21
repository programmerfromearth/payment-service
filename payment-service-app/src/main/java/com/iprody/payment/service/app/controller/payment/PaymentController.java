package com.iprody.payment.service.app.controller.payment;

import com.iprody.payment.service.app.controller.payment.model.PaymentFilterRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentToPartUpdateRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentResponse;
import com.iprody.payment.service.app.controller.payment.model.PaymentToCreateRequest;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

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

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("id") UUID id) {
        final Optional<PaymentResponse> result = paymentService.findPaymentById(id)
                .map(paymentMapper::toApiResponse);

        return ResponseEntity.of(result);
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentToCreateRequest toCreateRequest) {
        final PaymentDto toCreateDto = paymentMapper.toDto(toCreateRequest);

        final PaymentDto result = paymentService.create(toCreateDto);

        final PaymentResponse resultMapped = paymentMapper.toApiResponse(result);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resultMapped.guid())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(resultMapped);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(@PathVariable("id") UUID id,
                                                  @RequestBody PaymentToCreateRequest toCreateRequest) {
        final PaymentDto toCreateDto = paymentMapper.toDto(toCreateRequest);

        final PaymentDto result = paymentService.update(id, toCreateDto);

        final PaymentResponse resultMapped = paymentMapper.toApiResponse(result);

        return ResponseEntity.ok(resultMapped);
    }

    @PatchMapping("/{id}/note")
    public ResponseEntity<PaymentResponse> updatePart(@PathVariable("id") UUID id,
                                                      @RequestBody PaymentToPartUpdateRequest toPartUpdateRequest) {
        final PaymentDto result = paymentService.updateNote(id, toPartUpdateRequest);

        final PaymentResponse resultMapped = paymentMapper.toApiResponse(result);

        return ResponseEntity.ok(resultMapped);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
        paymentService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

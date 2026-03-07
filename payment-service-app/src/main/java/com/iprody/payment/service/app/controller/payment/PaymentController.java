package com.iprody.payment.service.app.controller.payment;

import com.iprody.payment.service.app.controller.payment.model.PaymentFilterRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentResponse;
import com.iprody.payment.service.app.controller.payment.model.PaymentToCreateRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentToPartUpdateRequest;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.service.payment.api.PaymentService;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public List<PaymentResponse> getAllPayments() {
        log.info("GET all payments");
        return paymentService.getAllPayments().stream()
                .map(paymentMapper::toApiResponse)
                .toList();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentResponse> searchPagedByFilter(@ModelAttribute PaymentFilterRequest paymentFilterRequest,
                                                     @PageableDefault(page = 0, size = 25) Pageable pageable) {
        log.info("GET all payments by search filter: {}", paymentFilterRequest);
        final PaymentFilter paymentFilter = paymentMapper.toPaymentFilter(paymentFilterRequest);
        return paymentService.searchPagedByFilter(paymentFilter, pageable)
                .map(paymentMapper::toApiResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentResponse getPaymentById(@PathVariable("id") UUID id) {
        log.info("GET payment by id: {}", id);
        final PaymentDto result = paymentService.getById(id);
        log.debug("Sending response PaymentDto: {}", result);
        return paymentMapper.toApiResponse(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentToCreateRequest toCreateRequest) {
        log.info("POST create payment: {}", toCreateRequest);
        final PaymentDto toCreateDto = paymentMapper.toDto(toCreateRequest);

        final PaymentDto result = paymentService.create(toCreateDto);

        final PaymentResponse resultMapped = paymentMapper.toApiResponse(result);
        log.debug("Sending response PaymentDto: {}", result);

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
    @PreAuthorize("hasRole('admin')")
    public PaymentResponse update(@PathVariable("id") UUID id,
                                  @RequestBody PaymentToCreateRequest toCreateRequest) {
        log.info("PUT update payment by id: {}, {}", id, toCreateRequest);
        final PaymentDto toCreateDto = paymentMapper.toDto(toCreateRequest);
        final PaymentDto result = paymentService.update(id, toCreateDto);
        log.debug("Sending response PaymentDto: {}", result);

        return paymentMapper.toApiResponse(result);
    }

    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('admin')")
    public PaymentResponse updatePart(@PathVariable("id") UUID id,
                                      @RequestBody PaymentToPartUpdateRequest toPartUpdateRequest) {
        log.info("PATCH partial update payment by id: {}, {}", id, toPartUpdateRequest);
        final PaymentDto result = paymentService.updateNote(id, toPartUpdateRequest);
        log.debug("Sending response PaymentDto: {}", result);
        return paymentMapper.toApiResponse(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void deleteById(@PathVariable("id") UUID id) {
        log.info("DELETE delete payment by id: {}", id);
        paymentService.deleteById(id);
    }
}

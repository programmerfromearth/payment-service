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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments().stream()
                .map(paymentMapper::toApiResponse)
                .toList();
    }

    @GetMapping("/search")
    public Page<PaymentResponse> searchPagedByFilter(@ModelAttribute PaymentFilterRequest paymentFilterRequest,
                                                     @PageableDefault(page = 0, size = 25) Pageable pageable) {
        final PaymentFilter paymentFilter = paymentMapper.toPaymentFilter(paymentFilterRequest);
        return paymentService.searchPagedByFilter(paymentFilter, pageable)
                .map(paymentMapper::toApiResponse);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable("id") UUID id) {
        final PaymentDto result = paymentService.getById(id);

        return paymentMapper.toApiResponse(result);
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
    public PaymentResponse update(@PathVariable("id") UUID id,
                                  @RequestBody PaymentToCreateRequest toCreateRequest) {
        final PaymentDto toCreateDto = paymentMapper.toDto(toCreateRequest);
        final PaymentDto result = paymentService.update(id, toCreateDto);

        return paymentMapper.toApiResponse(result);
    }

    @PatchMapping("/{id}/note")
    public PaymentResponse updatePart(@PathVariable("id") UUID id,
                                      @RequestBody PaymentToPartUpdateRequest toPartUpdateRequest) {
        final PaymentDto result = paymentService.updateNote(id, toPartUpdateRequest);

        return paymentMapper.toApiResponse(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") UUID id) {
        paymentService.deleteById(id);
    }
}

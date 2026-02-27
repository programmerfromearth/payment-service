package com.iprody.payment.service.app.mapper;

import com.iprody.payment.service.app.controller.payment.model.PaymentFilterRequest;
import com.iprody.payment.service.app.controller.payment.model.PaymentResponse;
import com.iprody.payment.service.app.controller.payment.model.PaymentToCreateRequest;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import com.iprody.payment.service.app.service.payment.model.PaymentDto;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.math.BigDecimal;
import java.util.UUID;

import static com.iprody.payment.service.app.persistency.entity.PaymentStatus.RECEIVED;
import static com.iprody.payment.service.app.util.TestConstants.OFFSET_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.quality.Strictness.STRICT_STUBS;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = STRICT_STUBS)
class PaymentMapperTest {

    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldNullWhenMapToDto() {
        // given
        final PaymentEntity entity = null;

        // when
        final PaymentDto dto = mapper.toDto(entity);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void shouldMapToDto() {
        // given
        final PaymentEntity entity = new PaymentEntity();
        entity.setGuid(UUID.randomUUID());
        entity.setInquiryRefId(UUID.randomUUID());
        entity.setAmount(BigDecimal.ZERO);
        entity.setCurrency("USD");
        entity.setTransactionRefId(UUID.randomUUID());
        entity.setStatus(RECEIVED);
        entity.setNote("note");
        entity.setCreatedAt(OFFSET_DATE_TIME);
        entity.setUpdatedAt(OFFSET_DATE_TIME);

        // when
        final PaymentDto dto = mapper.toDto(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.guid()).isEqualTo(entity.getGuid());
        assertThat(dto.inquiryRefId()).isEqualTo(entity.getInquiryRefId());
        assertThat(dto.amount()).isEqualTo(entity.getAmount());
        assertThat(dto.currency()).isEqualTo(entity.getCurrency());
        assertThat(dto.transactionRefId()).isEqualTo(entity.getTransactionRefId());
        assertThat(dto.status()).isEqualTo(entity.getStatus());
        assertThat(dto.note()).isEqualTo(entity.getNote());
        assertThat(dto.createdAt()).isEqualTo(entity.getCreatedAt());
        assertThat(dto.updatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    void shouldNullWhenMapToApiResponse() {
        // given
        final PaymentDto dto = null;

        // when
        final PaymentResponse apiResponse = mapper.toApiResponse(dto);

        // then
        assertThat(apiResponse).isNull();
    }

    @Test
    void shouldMapToApiResponse() {
        // given
        final PaymentDto dto = PaymentDto.builder()
                .guid(UUID.randomUUID())
                .inquiryRefId(UUID.randomUUID())
                .amount(BigDecimal.ZERO)
                .currency("USD")
                .transactionRefId(UUID.randomUUID())
                .status(RECEIVED)
                .note("note")
                .createdAt(OFFSET_DATE_TIME)
                .updatedAt(OFFSET_DATE_TIME)
                .build();

        // when
        final PaymentResponse apiResponse = mapper.toApiResponse(dto);

        // then
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.guid()).isEqualTo(dto.guid());
        assertThat(apiResponse.inquiryRefId()).isEqualTo(dto.inquiryRefId());
        assertThat(apiResponse.amount()).isEqualTo(dto.amount());
        assertThat(apiResponse.currency()).isEqualTo(dto.currency());
        assertThat(apiResponse.transactionRefId()).isEqualTo(dto.transactionRefId());
        assertThat(apiResponse.status()).isEqualTo(dto.status());
        assertThat(apiResponse.note()).isEqualTo(dto.note());
        assertThat(apiResponse.createdAt()).isEqualTo(dto.createdAt());
        assertThat(apiResponse.updatedAt()).isEqualTo(dto.updatedAt());
    }

    @Test
    void shouldNullWhenMapToPaymentFilter() {
        // given
        final PaymentFilterRequest filterRequest = null;

        // when
        final PaymentFilter filter = mapper.toPaymentFilter(filterRequest);

        // then
        assertThat(filter).isNull();
    }

    @Test
    void shouldMapToPaymentEntity() {
        // given
        final PaymentDto dto = PaymentDto.builder()
                .guid(UUID.randomUUID())
                .inquiryRefId(UUID.randomUUID())
                .amount(BigDecimal.ZERO)
                .currency("USD")
                .transactionRefId(UUID.randomUUID())
                .status(RECEIVED)
                .note("note")
                .createdAt(OFFSET_DATE_TIME)
                .updatedAt(OFFSET_DATE_TIME)
                .build();

        // when
        final PaymentEntity entity = mapper.toEntity(dto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(dto.guid());
        assertThat(entity.getInquiryRefId()).isEqualTo(dto.inquiryRefId());
        assertThat(entity.getAmount()).isEqualTo(dto.amount());
        assertThat(entity.getCurrency()).isEqualTo(dto.currency());
        assertThat(entity.getTransactionRefId()).isEqualTo(dto.transactionRefId());
        assertThat(entity.getStatus()).isEqualTo(dto.status());
        assertThat(entity.getNote()).isEqualTo(dto.note());
        assertThat(entity.getCreatedAt()).isEqualTo(dto.createdAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(dto.updatedAt());
    }

    @Test
    void shouldNullWhenMapToPaymentEntity() {
        // given
        final PaymentDto dto = null;

        // when
        final PaymentEntity filter = mapper.toEntity(dto);

        // then
        assertThat(filter).isNull();
    }

    @Test
    void shouldMapToPaymentDtoFromPaymentToCreateRequest() {
        // given
        final PaymentToCreateRequest toCreateRequest = PaymentToCreateRequest.builder()
                .inquiryRefId(UUID.randomUUID())
                .amount(BigDecimal.ZERO)
                .currency("USD")
                .transactionRefId(UUID.randomUUID())
                .status(RECEIVED)
                .note("note")
                .build();

        // when
        final PaymentDto dto = mapper.toDto(toCreateRequest);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.guid()).isNull();
        assertThat(dto.inquiryRefId()).isEqualTo(toCreateRequest.inquiryRefId());
        assertThat(dto.amount()).isEqualTo(toCreateRequest.amount());
        assertThat(dto.currency()).isEqualTo(toCreateRequest.currency());
        assertThat(dto.transactionRefId()).isEqualTo(toCreateRequest.transactionRefId());
        assertThat(dto.status()).isEqualTo(toCreateRequest.status());
        assertThat(dto.note()).isEqualTo(toCreateRequest.note());
        assertThat(dto.createdAt()).isNull();
        assertThat(dto.updatedAt()).isNull();
    }

    @Test
    void shouldNullWhenMapToPaymentDto() {
        // given
        final PaymentToCreateRequest toCreateRequest = null;

        // when
        final PaymentDto filter = mapper.toDto(toCreateRequest);

        // then
        assertThat(filter).isNull();
    }

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void shouldMapToPaymentFilter(PaymentStatus status) {
        // given
        final PaymentFilterRequest filterRequest = buildPaymentFilterRequestByStatus(status);

        // when
        final PaymentFilter apiResponse = mapper.toPaymentFilter(filterRequest);

        // then
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.status()).isEqualTo(filterRequest.status());
        assertThat(apiResponse.currency()).isEqualTo(filterRequest.currency());
        assertThat(apiResponse.minAmount()).isEqualTo(filterRequest.minAmount());
        assertThat(apiResponse.maxAmount()).isEqualTo(filterRequest.maxAmount());
        assertThat(apiResponse.createdAfter()).isEqualTo(filterRequest.createdAfter());
        assertThat(apiResponse.createdBefore()).isEqualTo(filterRequest.createdBefore());
    }

    private PaymentFilterRequest buildPaymentFilterRequestByStatus(PaymentStatus status) {
        return PaymentFilterRequest.builder()
                .status(status)
                .currency("USD")
                .minAmount(BigDecimal.ZERO)
                .maxAmount(BigDecimal.ONE)
                .createdAfter(OFFSET_DATE_TIME)
                .createdBefore(OFFSET_DATE_TIME)
                .build();
    }
}

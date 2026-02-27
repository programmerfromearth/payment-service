package com.iprody.payment.service.app.persistency.specification;

import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity_;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.iprody.payment.service.app.util.TestConstants.OFFSET_DATE_TIME;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.STRICT_STUBS;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = STRICT_STUBS)
class PaymentFilterFactoryTest {

    @Mock
    private Root<PaymentEntity> rootMock;
    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Test
    void fromFilterWhenAllFieldsFilled() {
        // given
        final PaymentStatus expectedStatus = PaymentStatus.RECEIVED;
        final String expectedCurrency = "USD";
        final BigDecimal expectedMinAmount = BigDecimal.ZERO;
        final BigDecimal expectedMaxAmount = BigDecimal.TWO;
        final OffsetDateTime expectedCreatedAfter = OFFSET_DATE_TIME;
        final OffsetDateTime expectedCreatedBefore = OFFSET_DATE_TIME;

        final PaymentFilter filter = PaymentFilter.builder()
                .status(expectedStatus)
                .currency(expectedCurrency)
                .minAmount(expectedMinAmount)
                .maxAmount(expectedMaxAmount)
                .createdAfter(expectedCreatedAfter)
                .createdBefore(expectedCreatedBefore)
                .build();

        final Path<PaymentStatus> paymentStatusPath = mock(Path.class);
        final Path<String> currencyPath = mock(Path.class);
        final Path<BigDecimal> amountPath = mock(Path.class);
        final Path<OffsetDateTime> createdAtPath = mock(Path.class);

        final Predicate statusPredicate = mock(Predicate.class);
        final Predicate currencyPredicate = mock(Predicate.class);
        final Predicate amountPredicate = mock(Predicate.class);
        final Predicate createdAtPredicate = mock(Predicate.class);

        final Predicate combined1 = mock(Predicate.class);
        final Predicate combined2 = mock(Predicate.class);
        final Predicate combined3 = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.STATUS)).thenAnswer(ignored -> paymentStatusPath);
        when(criteriaBuilderMock.equal(paymentStatusPath, expectedStatus)).thenReturn(statusPredicate);

        when(rootMock.get(PaymentEntity_.CURRENCY)).thenAnswer(ignored -> currencyPath);
        when(criteriaBuilderMock.equal(currencyPath, expectedCurrency)).thenReturn(currencyPredicate);

        when(rootMock.get(PaymentEntity_.AMOUNT)).thenAnswer(ignored -> amountPath);
        when(criteriaBuilderMock.between(amountPath, expectedMinAmount, expectedMaxAmount)).thenReturn(amountPredicate);

        when(rootMock.get(PaymentEntity_.CREATED_AT)).thenAnswer(ignored -> createdAtPath);
        when(criteriaBuilderMock.between(createdAtPath, expectedCreatedAfter, expectedCreatedBefore))
                .thenReturn(createdAtPredicate);

        when(criteriaBuilderMock.and(statusPredicate, currencyPredicate)).thenReturn(combined1);
        when(criteriaBuilderMock.and(combined1, amountPredicate)).thenReturn(combined2);
        when(criteriaBuilderMock.and(combined2, createdAtPredicate)).thenReturn(combined3);

        // when
        PaymentFilterFactory.fromFilter(filter).toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        inOrder.verify(rootMock).get(PaymentEntity_.STATUS);
        inOrder.verify(criteriaBuilderMock).equal(paymentStatusPath, expectedStatus);

        inOrder.verify(rootMock).get(PaymentEntity_.CURRENCY);
        inOrder.verify(criteriaBuilderMock).equal(currencyPath, expectedCurrency);
        inOrder.verify(criteriaBuilderMock).and(statusPredicate, currencyPredicate);

        inOrder.verify(rootMock).get(PaymentEntity_.AMOUNT);
        inOrder.verify(criteriaBuilderMock).between(amountPath, expectedMinAmount, expectedMaxAmount);
        inOrder.verify(criteriaBuilderMock).and(combined1, amountPredicate);

        inOrder.verify(rootMock).get(PaymentEntity_.CREATED_AT);
        inOrder.verify(criteriaBuilderMock).between(createdAtPath, expectedCreatedAfter, expectedCreatedBefore);
        inOrder.verify(criteriaBuilderMock).and(combined2, createdAtPredicate);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void fromFilterWhenAllFieldsFilled1() {
        // given
        final PaymentStatus expectedStatus = PaymentStatus.RECEIVED;
        final String expectedCurrency = "USD";
        final BigDecimal expectedMinAmount = BigDecimal.ZERO;
        final BigDecimal expectedMaxAmount = null;
        final OffsetDateTime expectedCreatedAfter = OFFSET_DATE_TIME;
        final OffsetDateTime expectedCreatedBefore = null;

        final PaymentFilter filter = PaymentFilter.builder()
                .status(expectedStatus)
                .currency(expectedCurrency)
                .minAmount(expectedMinAmount)
                .maxAmount(expectedMaxAmount)
                .createdAfter(expectedCreatedAfter)
                .createdBefore(expectedCreatedBefore)
                .build();

        final Path<PaymentStatus> paymentStatusPath = mock(Path.class);
        final Path<String> currencyPath = mock(Path.class);
        final Path<BigDecimal> amountPath = mock(Path.class);
        final Path<OffsetDateTime> createdAtPath = mock(Path.class);

        final Predicate statusPredicate = mock(Predicate.class);
        final Predicate currencyPredicate = mock(Predicate.class);
        final Predicate amountPredicate = mock(Predicate.class);
        final Predicate createdAtPredicate = mock(Predicate.class);

        final Predicate combined1 = mock(Predicate.class);
        final Predicate combined2 = mock(Predicate.class);
        final Predicate combined3 = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.STATUS)).thenAnswer(ignored -> paymentStatusPath);
        when(criteriaBuilderMock.equal(paymentStatusPath, expectedStatus)).thenReturn(statusPredicate);

        when(rootMock.get(PaymentEntity_.CURRENCY)).thenAnswer(ignored -> currencyPath);
        when(criteriaBuilderMock.equal(currencyPath, expectedCurrency)).thenReturn(currencyPredicate);

        when(rootMock.get(PaymentEntity_.AMOUNT)).thenAnswer(ignored -> amountPath);
        when(criteriaBuilderMock.greaterThanOrEqualTo(amountPath, expectedMinAmount)).thenReturn(amountPredicate);

        when(rootMock.get(PaymentEntity_.CREATED_AT)).thenAnswer(ignored -> createdAtPath);
        when(criteriaBuilderMock.greaterThanOrEqualTo(createdAtPath, expectedCreatedAfter))
                .thenReturn(createdAtPredicate);

        when(criteriaBuilderMock.and(statusPredicate, currencyPredicate)).thenReturn(combined1);
        when(criteriaBuilderMock.and(combined1, amountPredicate)).thenReturn(combined2);
        when(criteriaBuilderMock.and(combined2, createdAtPredicate)).thenReturn(combined3);

        // when
        PaymentFilterFactory.fromFilter(filter).toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        inOrder.verify(rootMock).get(PaymentEntity_.STATUS);
        inOrder.verify(criteriaBuilderMock).equal(paymentStatusPath, expectedStatus);

        inOrder.verify(rootMock).get(PaymentEntity_.CURRENCY);
        inOrder.verify(criteriaBuilderMock).equal(currencyPath, expectedCurrency);
        inOrder.verify(criteriaBuilderMock).and(statusPredicate, currencyPredicate);

        inOrder.verify(rootMock).get(PaymentEntity_.AMOUNT);
        inOrder.verify(criteriaBuilderMock).greaterThanOrEqualTo(amountPath, expectedMinAmount);
        inOrder.verify(criteriaBuilderMock).and(combined1, amountPredicate);

        inOrder.verify(rootMock).get(PaymentEntity_.CREATED_AT);
        inOrder.verify(criteriaBuilderMock).greaterThanOrEqualTo(createdAtPath, expectedCreatedAfter);
        inOrder.verify(criteriaBuilderMock).and(combined2, createdAtPredicate);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void fromFilterWhenAllFieldsFilled2() {
        // given
        final PaymentStatus expectedStatus = PaymentStatus.RECEIVED;
        final String expectedCurrency = "USD";
        final BigDecimal expectedMinAmount = null;
        final BigDecimal expectedMaxAmount = BigDecimal.TWO;
        final OffsetDateTime expectedCreatedAfter = null;
        final OffsetDateTime expectedCreatedBefore = OFFSET_DATE_TIME;

        final PaymentFilter filter = PaymentFilter.builder()
                .status(expectedStatus)
                .currency(expectedCurrency)
                .minAmount(expectedMinAmount)
                .maxAmount(expectedMaxAmount)
                .createdAfter(expectedCreatedAfter)
                .createdBefore(expectedCreatedBefore)
                .build();

        final Path<PaymentStatus> paymentStatusPath = mock(Path.class);
        final Path<String> currencyPath = mock(Path.class);
        final Path<BigDecimal> amountPath = mock(Path.class);
        final Path<OffsetDateTime> createdAtPath = mock(Path.class);

        final Predicate statusPredicate = mock(Predicate.class);
        final Predicate currencyPredicate = mock(Predicate.class);
        final Predicate amountPredicate = mock(Predicate.class);
        final Predicate createdAtPredicate = mock(Predicate.class);

        final Predicate combined1 = mock(Predicate.class);
        final Predicate combined2 = mock(Predicate.class);
        final Predicate combined3 = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.STATUS)).thenAnswer(ignored -> paymentStatusPath);
        when(criteriaBuilderMock.equal(paymentStatusPath, expectedStatus)).thenReturn(statusPredicate);

        when(rootMock.get(PaymentEntity_.CURRENCY)).thenAnswer(ignored -> currencyPath);
        when(criteriaBuilderMock.equal(currencyPath, expectedCurrency)).thenReturn(currencyPredicate);

        when(rootMock.get(PaymentEntity_.AMOUNT)).thenAnswer(ignored -> amountPath);
        when(criteriaBuilderMock.lessThanOrEqualTo(amountPath, expectedMaxAmount)).thenReturn(amountPredicate);

        when(rootMock.get(PaymentEntity_.CREATED_AT)).thenAnswer(ignored -> createdAtPath);
        when(criteriaBuilderMock.lessThanOrEqualTo(createdAtPath, expectedCreatedBefore))
                .thenReturn(createdAtPredicate);

        when(criteriaBuilderMock.and(statusPredicate, currencyPredicate)).thenReturn(combined1);
        when(criteriaBuilderMock.and(combined1, amountPredicate)).thenReturn(combined2);
        when(criteriaBuilderMock.and(combined2, createdAtPredicate)).thenReturn(combined3);

        // when
        PaymentFilterFactory.fromFilter(filter).toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        inOrder.verify(rootMock).get(PaymentEntity_.STATUS);
        inOrder.verify(criteriaBuilderMock).equal(paymentStatusPath, expectedStatus);

        inOrder.verify(rootMock).get(PaymentEntity_.CURRENCY);
        inOrder.verify(criteriaBuilderMock).equal(currencyPath, expectedCurrency);
        inOrder.verify(criteriaBuilderMock).and(statusPredicate, currencyPredicate);

        inOrder.verify(rootMock).get(PaymentEntity_.AMOUNT);
        inOrder.verify(criteriaBuilderMock).lessThanOrEqualTo(amountPath, expectedMaxAmount);
        inOrder.verify(criteriaBuilderMock).and(combined1, amountPredicate);

        inOrder.verify(rootMock).get(PaymentEntity_.CREATED_AT);
        inOrder.verify(criteriaBuilderMock).lessThanOrEqualTo(createdAtPath, expectedCreatedBefore);
        inOrder.verify(criteriaBuilderMock).and(combined2, createdAtPredicate);
        inOrder.verifyNoMoreInteractions();
    }
}

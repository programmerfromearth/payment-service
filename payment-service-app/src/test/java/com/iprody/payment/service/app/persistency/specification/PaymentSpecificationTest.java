package com.iprody.payment.service.app.persistency.specification;

import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity_;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.iprody.payment.service.app.persistency.entity.PaymentStatus.RECEIVED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.quality.Strictness.STRICT_STUBS;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = STRICT_STUBS)
class PaymentSpecificationTest {

    @Mock
    private Root<PaymentEntity> rootMock;
    @Mock
    private CriteriaQuery<?> criteriaQueryMock;
    @Mock
    private CriteriaBuilder criteriaBuilderMock;

    @Test
    void hasStatus() {
        // given
        final PaymentStatus status = RECEIVED;
        final Path<PaymentStatus> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.STATUS)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.equal(path, status)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.hasStatus(RECEIVED)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.STATUS);
        inOrder.verify(criteriaBuilderMock).equal(path, status);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void hasCurrency() {
        // given
        final String currency = "USD";
        final Path<String> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.CURRENCY)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.equal(path, currency)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.hasCurrency(currency)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.CURRENCY);
        inOrder.verify(criteriaBuilderMock).equal(path, currency);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void amountBetween() {
        // given
        final BigDecimal min = BigDecimal.ZERO;
        final BigDecimal max = BigDecimal.TWO;
        final Path<BigDecimal> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.AMOUNT)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.between(path, min, max)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.amountBetween(min, max)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.AMOUNT);
        inOrder.verify(criteriaBuilderMock).between(path, min, max);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void amountGreater() {
        // given
        final BigDecimal min = BigDecimal.ZERO;
        final Path<BigDecimal> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.AMOUNT)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.greaterThanOrEqualTo(path, min)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.amountGreater(min)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.AMOUNT);
        inOrder.verify(criteriaBuilderMock).greaterThanOrEqualTo(path, min);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void amountLess() {
        // given
        final BigDecimal max = BigDecimal.ZERO;
        final Path<BigDecimal> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.AMOUNT)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.lessThanOrEqualTo(path, max)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.amountLess(max)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.AMOUNT);
        inOrder.verify(criteriaBuilderMock).lessThanOrEqualTo(path, max);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void createdBetween() {
        // given
        final OffsetDateTime after = OffsetDateTime.now();
        final OffsetDateTime before = OffsetDateTime.now();
        final Path<OffsetDateTime> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.CREATED_AT)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.between(path, after, before)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.createdBetween(after, before)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.CREATED_AT);
        inOrder.verify(criteriaBuilderMock).between(path, after, before);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void createdGreater() {
        // given
        final OffsetDateTime after = OffsetDateTime.now();
        final Path<OffsetDateTime> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.CREATED_AT)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.greaterThanOrEqualTo(path, after)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.createdGreater(after)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.CREATED_AT);
        inOrder.verify(criteriaBuilderMock).greaterThanOrEqualTo(path, after);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void createdLess() {
        // given
        final OffsetDateTime before = OffsetDateTime.now();
        final Path<OffsetDateTime> path = mock(Path.class);
        final Predicate expectedPredicate = mock(Predicate.class);

        when(rootMock.get(PaymentEntity_.CREATED_AT)).thenAnswer(ignored -> path);
        when(criteriaBuilderMock.lessThanOrEqualTo(path, before)).thenReturn(expectedPredicate);

        // when
        final Predicate actualPredicate = PaymentSpecification.createdLess(before)
                .toPredicate(rootMock, criteriaQueryMock, criteriaBuilderMock);

        // then
        final InOrder inOrder = inOrder(rootMock, criteriaQueryMock, criteriaBuilderMock);
        assertThat(actualPredicate).isEqualTo(expectedPredicate);
        inOrder.verify(rootMock).get(PaymentEntity_.CREATED_AT);
        inOrder.verify(criteriaBuilderMock).lessThanOrEqualTo(path, before);
        inOrder.verifyNoMoreInteractions();
    }
}
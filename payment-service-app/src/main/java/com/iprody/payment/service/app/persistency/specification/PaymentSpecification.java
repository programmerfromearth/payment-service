package com.iprody.payment.service.app.persistency.specification;

import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.entity.PaymentEntity_;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class PaymentSpecification {

    public static Specification<PaymentEntity> hasStatus(PaymentStatus status) {
        return (root, query, cb) -> cb.equal(root.get(PaymentEntity_.STATUS), status);
    }

    public static Specification<PaymentEntity> hasCurrency(String currency) {
        return (root, query, cb) -> cb.equal(root.get(PaymentEntity_.CURRENCY), currency);
    }

    public static Specification<PaymentEntity> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> cb.between(root.get(PaymentEntity_.AMOUNT), min, max);
    }

    public static Specification<PaymentEntity> amountGreater(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(PaymentEntity_.AMOUNT), min);
    }

    public static Specification<PaymentEntity> amountLess(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(PaymentEntity_.AMOUNT), max);
    }

    public static Specification<PaymentEntity> createdBetween(OffsetDateTime after, OffsetDateTime before) {
        return (root, query, cb) -> cb.between(root.get(PaymentEntity_.CREATED_AT), after, before);
    }

    public static Specification<PaymentEntity> createdGreater(OffsetDateTime after) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(PaymentEntity_.CREATED_AT), after);
    }

    public static Specification<PaymentEntity> createdLess(OffsetDateTime before) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get(PaymentEntity_.CREATED_AT), before);
    }
}

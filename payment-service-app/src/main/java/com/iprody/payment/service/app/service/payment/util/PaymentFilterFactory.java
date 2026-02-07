package com.iprody.payment.service.app.service.payment.util;

import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import com.iprody.payment.service.app.persistency.specification.PaymentSpecification;
import com.iprody.payment.service.app.service.payment.model.PaymentFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class PaymentFilterFactory {

    public static Specification<PaymentEntity> fromFilter(PaymentFilter filter) {
        return Specification.<PaymentEntity>unrestricted()
                .and(getPaymentEntitySpecification(filter))
                .and(getAmountSpecification(filter))
                .and(getcreatedAfterSpecification(filter));
    }

    private static Specification<PaymentEntity> getPaymentEntitySpecification(PaymentFilter filter) {
        if (StringUtils.hasText(filter.currency())) {
            return PaymentSpecification.hasCurrency(filter.currency());
        }

        return Specification.unrestricted();
    }

    private static Specification<PaymentEntity> getAmountSpecification(PaymentFilter filter) {
        if (filter.minAmount() != null && filter.maxAmount() != null) {
            return PaymentSpecification.amountBetween(filter.minAmount(), filter.maxAmount());
        }

        if (filter.minAmount() != null) {
            return PaymentSpecification.amountGreater(filter.minAmount());
        }

        if (filter.maxAmount() != null) {
            return PaymentSpecification.amountLess(filter.maxAmount());
        }

        return Specification.unrestricted();
    }

    private static Specification<PaymentEntity> getcreatedAfterSpecification(PaymentFilter filter) {
        if (filter.createdAfter() != null && filter.createdBefore() != null) {
            return PaymentSpecification.createdBetween(filter.createdAfter(), filter.createdBefore());
        }

        if (filter.createdAfter() != null) {
            return PaymentSpecification.createdGreater(filter.createdAfter());
        }

        if (filter.createdBefore() != null) {
            return PaymentSpecification.createdLess(filter.createdBefore());
        }

        return Specification.unrestricted();
    }
}

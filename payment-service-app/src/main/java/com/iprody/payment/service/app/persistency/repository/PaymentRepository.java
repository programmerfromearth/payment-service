package com.iprody.payment.service.app.persistency.repository;

import com.iprody.payment.service.app.persistency.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

}

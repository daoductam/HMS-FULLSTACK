package com.hms.PaymentMS.repository;

import com.hms.PaymentMS.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByOrderId(String orderId);
    Optional<PaymentTransaction> findByTransactionId(String transactionId);
}


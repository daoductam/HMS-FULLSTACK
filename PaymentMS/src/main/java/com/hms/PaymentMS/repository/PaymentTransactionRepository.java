package com.hms.PaymentMS.repository;

import com.hms.PaymentMS.entity.PaymentTransaction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PaymentTransactionRepository implements PanacheRepository<PaymentTransaction> {
    public Optional<PaymentTransaction> findByOrderId(String orderId) {
        return find("orderId", orderId).firstResultOptional();
    }

    public Optional<PaymentTransaction> findByTransactionId(String transactionId) {
        return find("transactionId", transactionId).firstResultOptional();
    }
}

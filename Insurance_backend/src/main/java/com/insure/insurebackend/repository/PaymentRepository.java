package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.Payment;
import com.insure.insurebackend.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCustomerId(Long customerId);

    long countByStatus(PaymentStatus status);

    @Query("select coalesce(sum(p.amount),0) from Payment p where p.status = :status and p.paidAt between :start and :end")
    BigDecimal sumByStatusAndDate(@Param("status") PaymentStatus status,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    @Query("select coalesce(sum(p.amount),0) from Payment p where p.status = :status")
    BigDecimal sumByStatus(@Param("status") PaymentStatus status);
}

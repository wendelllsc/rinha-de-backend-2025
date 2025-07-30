package br.com.wlsc.api.domain.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    @Query("SELECT p FROM " +
            "Payment p " +
            "WHERE p.requestedAt >= :from AND p.requestedAt <= :to")
    Page<Payment> findByDate(OffsetDateTime from, OffsetDateTime to, Pageable pageable);
}

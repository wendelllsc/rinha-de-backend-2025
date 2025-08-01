package br.com.wlsc.api.domain.payment;

import br.com.wlsc.api.domain.dto.PaymentDto;
import br.com.wlsc.api.worker.PaymentWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Component
@Slf4j
public class PaymentComponent {

    private final PaymentWorker paymentWorker;
    private final PaymentService paymentService;

    @Autowired
    public PaymentComponent(PaymentWorker paymentWorker,
                            PaymentService paymentService) {
        this.paymentWorker = paymentWorker;
        this.paymentService = paymentService;
    }

    public void processPayment(PaymentDto paymentDto) {
        Payment payment = Payment.builder()
                .correlationId(paymentDto.correlationId())
                .amount(paymentDto.amount())
                .requestedAt(Instant.now())
                .build();
        paymentWorker.enqueue(payment);
    }

    public void purgePayments() {
        paymentService.purgePayments();
    }
}

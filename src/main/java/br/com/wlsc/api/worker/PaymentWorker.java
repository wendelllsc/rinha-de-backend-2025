package br.com.wlsc.api.worker;

import br.com.wlsc.api.client.processor.ProcessorService;
import br.com.wlsc.api.client.processor.ProcessorType;
import br.com.wlsc.api.domain.dto.PaymentDto;
import br.com.wlsc.api.domain.payment.Payment;
import br.com.wlsc.api.domain.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@Slf4j
public class PaymentWorker {

    @Value("${app.worker.pool-size}")
    private Integer workerPoolSize;

    private final PaymentService paymentService;
    private final ProcessorService processorService;
    private final LinkedBlockingDeque<PaymentDto> queue;

    @Autowired
    public PaymentWorker(PaymentService paymentService,
                         ProcessorService processorService,
                         LinkedBlockingDeque<PaymentDto> queue) {
        this.paymentService = paymentService;
        this.processorService = processorService;
        this.queue = queue;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWorkers() {
        log.info("Starting workers");
        for (int i = 0; i < workerPoolSize; i++) {
            Thread.startVirtualThread(this::runWorker);
        }
    }

    private void runWorker() {
        while (true) {
            PaymentDto payment = takePayment();
            processPayment(payment);
        }
    }

    private void processPayment(PaymentDto paymentDto) {
        Payment payment = Payment.builder()
                .correlationId(paymentDto.correlationId())
                .amount(paymentDto.amount())
                .requestedAt(Instant.now())
                .build();

        if (processorService.sendToDefaultWithRetry(payment)) {
            payment.setProcessor(ProcessorType.DEFAULT.name());
            sendPaymentDatabase(payment);
            return;
        }

        if (processorService.sendToFallback(payment)) {
            payment.setProcessor(ProcessorType.FALLBACK.name());
            sendPaymentDatabase(payment);
            return;
        }

        queue.offer(paymentDto);
    }

    private void sendPaymentDatabase(Payment payment){
        paymentService.savePayment(payment);
        log.info("Payment saved in the database");
    }

    private PaymentDto takePayment() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

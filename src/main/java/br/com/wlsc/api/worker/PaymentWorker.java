package br.com.wlsc.api.worker;

import br.com.wlsc.api.domain.payment.Payment;
import br.com.wlsc.api.domain.payment.PaymentComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.ArrayBlockingQueue;

@Component
@Slf4j
public class PaymentWorker {

    @Value("${app.worker.pool-size:15}")
    private Integer workerPoolSize;

    private final PaymentComponent paymentComponent;
    private final ArrayBlockingQueue<Payment> queue;

    @Autowired
    public PaymentWorker(PaymentComponent paymentComponent,
                         ArrayBlockingQueue<Payment> queue) {
        this.paymentComponent = paymentComponent;
        this.queue = queue;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startWorkers() {
        log.info("Starting workers");
        for (int i = 0; i < workerPoolSize; i++) {
            Thread.startVirtualThread(this::runWorker);
        }
    }

    public void enqueue(Payment payment) {
        payment.setRequestedAt(OffsetDateTime.now(ZoneId.of("Z")));
        queue.offer(payment);
    }

    private void runWorker() {
        while (true) {
            log.info("Run worker");
            Payment payment = takePayment();
            processPayment(payment);
        }
    }

    private void processPayment(Payment payment) {
        if(!this.paymentComponent.processPayment(payment)){
            enqueue(payment);
        }
    }

    private Payment takePayment() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

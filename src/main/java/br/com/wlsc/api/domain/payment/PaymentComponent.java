package br.com.wlsc.api.domain.payment;

import br.com.wlsc.api.client.processor.ProcessorService;
import br.com.wlsc.api.client.processor.ProcessorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentComponent {

    private final ProcessorService processorService;
    private final PaymentService paymentService;

    @Autowired
    public PaymentComponent(ProcessorService processorService,
                            PaymentService paymentService) {
        this.processorService = processorService;
        this.paymentService = paymentService;
    }

    public boolean processPayment(Payment payment) {
        if (processorService.sendToDefaultWithRetry(payment)) {
            payment.setProcessor(ProcessorType.DEFAULT);
            paymentService.save(payment);
            return true;
        }

        if (processorService.sendToFallback(payment)) {
            payment.setProcessor(ProcessorType.FALLBACK);
            paymentService.save(payment);
            return true;
        }

        return false;
    }
}

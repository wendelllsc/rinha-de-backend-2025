package br.com.wlsc.api.domain.summary;

import br.com.wlsc.api.domain.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SummaryComponent {

    private final PaymentService paymentService;

    @Autowired
    public SummaryComponent(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Summary createSummary(Instant from, Instant to) {
        return paymentService.getSummary(from, to);
    }
}

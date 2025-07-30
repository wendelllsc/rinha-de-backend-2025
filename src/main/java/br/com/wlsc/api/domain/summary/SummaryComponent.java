package br.com.wlsc.api.domain.summary;

import br.com.wlsc.api.client.processor.ProcessorType;
import br.com.wlsc.api.domain.payment.Payment;
import br.com.wlsc.api.domain.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class SummaryComponent {

    private final PaymentService paymentService;

    @Autowired
    public SummaryComponent(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Summary createSummary(OffsetDateTime from, OffsetDateTime to) {
        Page<Payment> payments;
        Pageable pageable = PageRequest.of(0, 50);
        SummaryDetail summaryDetailDefault = SummaryDetail.builder().build();
        SummaryDetail summaryDetailFallback = SummaryDetail.builder().build();

        do {
            payments = paymentService.findByDate(from, to, pageable);
            for (Payment payment : payments) {
                if (payment.getProcessor() == ProcessorType.DEFAULT) {
                    summaryDetailDefault.increaseTotalRequests();
                    summaryDetailDefault.addAmount(payment.getAmount());
                } else {
                    summaryDetailFallback.increaseTotalRequests();
                    summaryDetailFallback.addAmount(payment.getAmount());
                }
            }
            pageable = pageable.next();
        } while (payments.hasNext());

        return new Summary(summaryDetailDefault, summaryDetailFallback);
    }
}

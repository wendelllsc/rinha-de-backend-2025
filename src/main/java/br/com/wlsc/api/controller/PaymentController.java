package br.com.wlsc.api.controller;

import br.com.wlsc.api.domain.payment.Payment;
import br.com.wlsc.api.worker.PaymentWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    private final PaymentWorker paymentWorker;

    @Autowired
    public PaymentController(PaymentWorker paymentWorker) {
        this.paymentWorker = paymentWorker;
    }

    @PostMapping
    public void createPayment(@RequestBody Payment payment) {
        paymentWorker.enqueue(payment);
    }
}

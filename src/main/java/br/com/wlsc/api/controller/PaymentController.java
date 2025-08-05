package br.com.wlsc.api.controller;

import br.com.wlsc.api.domain.dto.PaymentDto;
import br.com.wlsc.api.domain.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.LinkedBlockingDeque;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    private final LinkedBlockingDeque<PaymentDto> queue;
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(LinkedBlockingDeque<PaymentDto> queue,
                             PaymentService paymentService) {
        this.queue = queue;
        this.paymentService = paymentService;
    }

    @PostMapping
    public void createPayment(@RequestBody PaymentDto payment) {
        queue.offer(payment);
    }

    @PostMapping("/purge")
    public void purgePayments() {
        paymentService.purgePayments();
    }
}

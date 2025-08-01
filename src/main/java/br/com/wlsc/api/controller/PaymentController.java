package br.com.wlsc.api.controller;

import br.com.wlsc.api.domain.dto.PaymentDto;
import br.com.wlsc.api.domain.payment.PaymentComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {

    private final ExecutorService executorService;
    private final PaymentComponent paymentComponent;

    @Autowired
    public PaymentController(ExecutorService executorService, PaymentComponent paymentComponent) {
        this.executorService = executorService;
        this.paymentComponent = paymentComponent;
    }

    @PostMapping
    public void createPayment(@RequestBody PaymentDto payment) {
        executorService.submit(() -> paymentComponent.processPayment(payment));
    }

    @PostMapping("/purge")
    public void purgePayments(){
        paymentComponent.purgePayments();
    }
}

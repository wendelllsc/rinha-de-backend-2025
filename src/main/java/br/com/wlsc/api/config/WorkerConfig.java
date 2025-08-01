package br.com.wlsc.api.config;

import br.com.wlsc.api.domain.payment.Payment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;

@Configuration
public class WorkerConfig {

    @Bean
    public LinkedBlockingDeque<Payment> queue() {
        return new LinkedBlockingDeque<>(10000);
    }

}

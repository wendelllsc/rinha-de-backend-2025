package br.com.wlsc.api.config;

import br.com.wlsc.api.domain.payment.Payment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;

@Configuration
public class WorkerConfig {

    @Bean
    public ArrayBlockingQueue<Payment> queue() {
        return new ArrayBlockingQueue<>(5000, false);
    }

}

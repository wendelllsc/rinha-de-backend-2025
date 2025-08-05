package br.com.wlsc.api.config;

import br.com.wlsc.api.domain.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;

@Configuration
public class WorkerConfig {

    @Value("${app.worker.queue-size}")
    private int QUEUE_SIZE;

    @Bean
    public LinkedBlockingDeque<PaymentDto> queue() {
        return new LinkedBlockingDeque<>(QUEUE_SIZE);
    }
}

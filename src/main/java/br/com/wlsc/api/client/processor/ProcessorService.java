package br.com.wlsc.api.client.processor;

import br.com.wlsc.api.domain.payment.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Service
@Slf4j
public class ProcessorService {

    @Value("${app.client.processor.max-retries}")
    private int maxRetries;
    @Value("${app.client.processor.defalt-payment}")
    private String defaultProcessorUrl;
    @Value("${app.client.processor.fallback-payment}")
    private String fallbackProcessorUrl;
    @Value("${app.client.web.connection-timeout}")
    private int timeout;

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Autowired
    public ProcessorService(ObjectMapper objectMapper, WebClient webClient) {
        this.objectMapper = objectMapper;
        this.webClient = webClient;
    }

    public boolean sendToDefaultWithRetry(Payment payment) {
        int attempt = 0;
        do {
            if (callClientProcessor(payment, ProcessorType.DEFAULT)) {
                return true;
            }
            attempt++;
        } while (attempt <= maxRetries);
        return false;
    }

    public boolean sendToFallback(Payment payment) {
        return callClientProcessor(payment, ProcessorType.FALLBACK);
    }

    public boolean callClientProcessor(Payment payment, ProcessorType processorType) {
        String url = processorType == ProcessorType.DEFAULT ? defaultProcessorUrl : fallbackProcessorUrl;
        String json;

        try {
            json = objectMapper.writeValueAsString(payment);
        } catch (JsonProcessingException e) {
            log.error("JSON converter error: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("Sending {}", payment.getCorrelationId());
        boolean response = Boolean.TRUE.equals(webClient.post()
                .uri(url)
                .bodyValue(json)
                .exchangeToMono(r -> Mono.just(r.statusCode().is2xxSuccessful()))
                .timeout(Duration.ofMillis(timeout))
                .doOnError(e -> log.error("Error sending payment {}: {}", payment.getCorrelationId(), e.getMessage()))
                .onErrorReturn(false)
                .block());
        log.info("Payment {} received successfully", payment.getCorrelationId());
        return response;
    }
}

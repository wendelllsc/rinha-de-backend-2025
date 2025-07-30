package br.com.wlsc.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${app.client.web.max-connections}")
    private int maxConnections;

    @Bean
    public WebClient webClient() {

        var provider = ConnectionProvider.builder("client")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofSeconds(10))
                .build();

        var httpClient = HttpClient.create(provider)
                .keepAlive(true)
                .compress(false)
                .wiretap(false);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}

package br.com.wlsc.api.domain.dto;

import java.math.BigDecimal;

public record PaymentDto(String correlationId, BigDecimal amount) {
}

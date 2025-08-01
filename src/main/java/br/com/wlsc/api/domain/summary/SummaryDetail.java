package br.com.wlsc.api.domain.summary;

import java.math.BigDecimal;

public record SummaryDetail(int totalRequests, BigDecimal totalAmount) {
}

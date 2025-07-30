package br.com.wlsc.api.domain.summary;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SummaryDetail {

    @Builder.Default
    private int totalRequests = 0;
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public void increaseTotalRequests() {
        this.totalRequests++;
    }

    public void addAmount(BigDecimal amount) {
        setTotalAmount(new BigDecimal(String.valueOf(getTotalAmount().add(amount))));
    }
}

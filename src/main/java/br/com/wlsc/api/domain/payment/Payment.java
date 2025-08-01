package br.com.wlsc.api.domain.payment;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {

    private String correlationId;
    private BigDecimal amount;
    private Instant requestedAt;
    private String processor;
}

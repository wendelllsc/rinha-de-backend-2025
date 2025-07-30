package br.com.wlsc.api.domain.payment;

import br.com.wlsc.api.client.processor.ProcessorType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Table(name = "payments")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {

    @Id
    @Column(name = "correlation_id")
    private String correlationId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "requested_at")
    private OffsetDateTime requestedAt;

    @Column(name = "processor")
    @Enumerated(EnumType.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ProcessorType processor;
}

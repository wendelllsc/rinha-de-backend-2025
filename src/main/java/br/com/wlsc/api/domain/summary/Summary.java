package br.com.wlsc.api.domain.summary;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Summary(@JsonProperty("default") SummaryDetail defaultSummary,
                      @JsonProperty("fallback") SummaryDetail fallbackSumamry) {
}

package br.com.wlsc.api.controller;

import br.com.wlsc.api.domain.summary.Summary;
import br.com.wlsc.api.domain.summary.SummaryComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/payments-summary")
public class SummaryController {

    private final SummaryComponent summaryComponent;

    @Autowired
    public SummaryController(SummaryComponent summaryComponent) {
        this.summaryComponent = summaryComponent;
    }

    @GetMapping
    public Summary summary(@RequestParam(value = "from", required = false) Instant from,
                           @RequestParam(value = "to", required = false) Instant to){
        return summaryComponent.createSummary(from, to);
    }
}

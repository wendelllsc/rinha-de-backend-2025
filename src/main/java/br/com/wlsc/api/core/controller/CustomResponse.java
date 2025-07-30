package br.com.wlsc.api.core.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class CustomResponse {

    private int status;
    private Object message;
    private OffsetDateTime time;
}

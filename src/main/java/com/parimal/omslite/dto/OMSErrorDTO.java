package com.parimal.omslite.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
public class OMSErrorDTO {
    private HttpStatus errorCode;
    private String message;
}

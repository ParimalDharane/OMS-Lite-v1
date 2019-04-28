package com.parimal.omslite.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class OMSException extends RuntimeException {

    private String message;
    private HttpStatus errorCode = HttpStatus.NOT_FOUND; //TODO create an internal code that can be mapped to HttpStatus

    public OMSException() {
        super();
    }
    public OMSException(String message, HttpStatus errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
    public String getMessage() {
        return this.message;
    }
    public static OMSException createNotFound(String message) {
        return new OMSException(message, HttpStatus.NOT_FOUND);
    }
    public static OMSException createUnprocessableEntity(String message) {
        return new OMSException(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
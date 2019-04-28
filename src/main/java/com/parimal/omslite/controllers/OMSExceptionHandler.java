package com.parimal.omslite.controllers;

import com.parimal.omslite.services.OMSException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception handler for OMSException
 * @see OMSException
 */
@RestControllerAdvice
public class OMSExceptionHandler {

    @RequestMapping(produces = "application/json")
    @ExceptionHandler(OMSException.class)
    public ResponseEntity handleOMSException(OMSException ex) {
        return ResponseEntity.status(ex.getErrorCode()).body(DTOUtils.toOMSErrorDTO(ex));
    }
}
package com.example.market.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadRequestException extends RuntimeException{
    private static final String MESSAGE = "Bad request";
    public BadRequestException(){
        super(MESSAGE);
        log.error(MESSAGE);
    }
}

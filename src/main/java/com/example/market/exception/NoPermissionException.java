package com.example.market.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoPermissionException extends RuntimeException{
    private static final String MESSAGE = "No permission";
    public NoPermissionException(){
        super(MESSAGE);
        log.error(MESSAGE);
    }
}

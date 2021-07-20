package com.example.market.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends RuntimeException{
    private static final String MESSAGE = "User Entity does not exist";
    public UserNotFoundException(){
        super(MESSAGE);
        log.error(MESSAGE);
    }
}

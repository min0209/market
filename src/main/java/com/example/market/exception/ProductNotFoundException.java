package com.example.market.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductNotFoundException extends RuntimeException{
    private static final String MESSAGE = "Product Entity does not exist";
    public ProductNotFoundException(){
        super(MESSAGE);
        log.error(MESSAGE);
    }
}

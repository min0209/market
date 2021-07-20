package com.example.market.model.enumclass;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Auth {
    admin(0,"admin"),
    manager(1,"manager"),
    user(2,"user")
    ;
    private int id;
    private String auth;
}

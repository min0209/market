package com.example.market.UserDetails;

import org.springframework.security.core.userdetails.User;

public class UserDetailsImpl extends User {
    public UserDetailsImpl(com.example.market.model.entity.User user) {
        super(user.getUsername(), user.getUsername(), user.getAuthorities());
    }
}

package com.example.market.model.network.request;

import com.example.market.model.enumclass.Area;
import com.example.market.model.enumclass.Auth;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApiRequest {

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Area area;

    public UserApiRequest(String username, String password){
        this.username = username;
        this.password = password;

    }

}

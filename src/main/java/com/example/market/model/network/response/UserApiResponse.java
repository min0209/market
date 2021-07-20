package com.example.market.model.network.response;

import com.example.market.model.enumclass.Area;
import com.example.market.model.enumclass.Auth;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApiResponse {

    private Long uid;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private Auth auth;

    private LocalDateTime signUpDate;

    private LocalDateTime lastLoginDate;

    private Area area;

    private boolean isDeleted;

    public UserApiResponse(String username, String nickname,Area area){
        this.username = username;
        this.nickname=nickname;
        this.area=area;
    }

}

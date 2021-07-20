package com.example.market.model.entity;

import com.example.market.model.enumclass.Area;
import com.example.market.model.enumclass.Auth;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"productList","password","bookmarkList"})
@Where(clause = "is_deleted = false")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @NotBlank(message = "user.username must not be blank")
    @Length(min = 4,max = 30)
    private String username;

    @JsonIgnore
    @NotBlank(message = "user.password must not be blank")
    @Length(max = 70)
    private String password;

    @NotBlank(message = "user.nickname must not be blank")
    @Length(min = 2,max = 20)
    private String nickname;

    private LocalDateTime signUpDate;

    private LocalDateTime lastLoginDate;

    @Enumerated(EnumType.STRING)
    private Auth auth;

    @NotNull(message = "userDto.area may not be null")
    @Enumerated(EnumType.STRING)
    private Area area;

    @NotBlank(message = "user.email must not be blank")
    @Email(message = "user.email must be email")
    private String email;

    private boolean isDeleted;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Product> productList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmarkList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof User)) return false;
        User user = (User) o;
        return getUid().equals(user.getUid());
    }
}

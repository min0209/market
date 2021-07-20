package com.example.market.config;

import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/login"," /user/signUp").anonymous()
                .antMatchers("/","/product","/product/{\\\\d+}").permitAll()
                .antMatchers("/user/logout","/user/myInfo","/user/myInfo/edit",
                        "/product/create").authenticated()
                .antMatchers("/admin").hasRole("admin")
                .antMatchers("/product/{\\\\s+}").denyAll()
//        .and()
//            .formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/")
//        .and()
//            .logout()
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
        .and()
            .cors().and().csrf().disable()
        ;
    }

}

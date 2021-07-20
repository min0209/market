package com.example.market.service;

import com.example.market.UserDetails.UserDetailsImpl;
import com.example.market.exception.NoPermissionException;
import com.example.market.exception.UserNotFoundException;
import com.example.market.model.enumclass.Area;
import com.example.market.model.network.Header;
import com.example.market.model.entity.User;
import com.example.market.model.enumclass.Auth;
import com.example.market.model.network.request.UserApiRequest;
import com.example.market.model.network.response.UserApiResponse;
import com.example.market.repository.BookmarkRepository;
import com.example.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Validated
@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Header create(@Valid UserApiRequest userApiRequest){

        User user = User.builder()
                .username(userApiRequest.getUsername())
                .password(userApiRequest.getPassword())
                .nickname(userApiRequest.getNickname())
                .email(userApiRequest.getEmail())
                .signUpDate(LocalDateTime.now())
                .area(userApiRequest.getArea())
                .auth(Auth.user)
                .isDeleted(false)
                .build();

            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            return Header.OK();

    }
//
//    public Header<UserApiResponse> getPersonInfo(String nickname){
//        User getUser = userRepository.findByNickname(nickname).get();
//        if(getUser == null){
//            return Header.ERROR("user not found");
//        }
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        return Header.OK(response(getUser, Optional.ofNullable(auth)));
//    }

    public Header<UserApiResponse> getSelfInfo(){
        if(!isLoggedIn().getData()){
            return Header.ERROR("no permission");
        }
        User getUser = userRepository.findByNickname(loggedInUser().getData()).get();
        if(getUser == null){
            return Header.ERROR("user not found");
        }
        return Header.OK(response(getUser));
    }

    public Header<UserApiResponse> getUserInfo(String username){
        Optional<User> getUser = userRepository.findByUsername(username);
        if(getUser.equals(Optional.empty())){
            return Header.ERROR("user not found");
        }
        UserApiResponse response = response(getUser.get());
        response.setEmail(null);
        return Header.OK(response);
    }

    public Header update(@Valid UserApiRequest userApiRequest){

        Authentication auth = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication()).orElseThrow(NoPermissionException::new);

        User loggedInUser = userRepository.findByUsername(auth.getName()).orElseThrow(UserNotFoundException::new);
        Optional<User> getUser = userRepository.findByUsername(userApiRequest.getUsername());
        if(getUser.equals(Optional.empty())){
            return Header.ERROR("user not found");
        }
        User user = getUser.get();
        if(!user.equals(loggedInUser)&&loggedInUser.getAuth().equals(Auth.user)){
            return Header.OK("no permission");
        }
        user.setNickname(userApiRequest.getNickname());
        if(!userApiRequest.getPassword().isEmpty()){
            user.setPassword(encoder.encode(userApiRequest.getPassword()));
        }
        user.setEmail(userApiRequest.getEmail());
        user.setArea(userApiRequest.getArea());

        userRepository.save(user);
        return Header.OK();
    }
    public Header delete(String username){
        Authentication auth = Optional.ofNullable(SecurityContextHolder
                .getContext().getAuthentication()).orElseThrow(NoPermissionException::new);

        User loggedInUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(UserNotFoundException::new);
        User getUser = userRepository.findByUsername(username).map(user -> {
            if(user.equals(loggedInUser)||!loggedInUser.getAuth()
                    .equals(Auth.user)) user.setDeleted(true);
            else throw new NoPermissionException();
            return user;
        }).orElseThrow(UserNotFoundException::new);
        userRepository.save(getUser);
        return Header.OK();
    }

    public Header login(UserApiRequest userApiRequest){
        Optional<User> optional = userRepository.findByUsername(userApiRequest.getUsername());
        System.out.println(encoder.matches(userApiRequest.getPassword(),optional.get().getPassword()));
        return optional.filter
                (user -> encoder.matches(userApiRequest.getPassword(),user.getPassword()))
        .map( user -> {
            ArrayList<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority(user.getAuth().getAuth()));
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword(),grantedAuths);
            SecurityContextHolder.getContext().setAuthentication(auth);
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
            return auth;
        }).map(auth -> Header.OK(auth))
                .orElseGet(() -> Header.ERROR("wrong info"));
    }
    public Header logout(HttpServletRequest request, HttpServletResponse response){
        new SecurityContextLogoutHandler().logout(request,response, SecurityContextHolder.getContext().getAuthentication());
        return Header.OK();
    }
    public Header likeList(){
        Authentication auth = Optional.ofNullable(SecurityContextHolder
            .getContext().getAuthentication()).orElseThrow(NoPermissionException::new);
//        List<Product> products = likeRepository.findByUser(userRepository.findByUsername(auth.getName())
//                .orElseThrow(UserNotFoundException::new));
        //return Header.OK(products);
        return null;
    }
    public Header<Boolean> isLoggedIn(){
        if(SecurityContextHolder.getContext().getAuthentication() ==null||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser"){
            return Header.OK(false);
        }
        else return Header.OK(true);
    }
    public Header<String> loggedInUser(){
        Authentication auth = Optional.ofNullable(SecurityContextHolder
                .getContext().getAuthentication()).orElseThrow(NoPermissionException::new);
        String nickname = userRepository.findByUsername(auth.getName()).map(user -> user.getNickname())
                .orElseThrow(UserNotFoundException::new);
        return Header.OK(nickname);
    }
    public Header<Area> getUserArea(Long uid){
        return Header.OK(userRepository.findById(uid)
                .orElseThrow(UserNotFoundException::new).getArea());
    }
    public Header<Boolean> isUsernameExist(String username){
        System.out.println(username);
        Boolean result = userRepository.findByUsername(username).map(user -> true).orElseGet(() -> false);
        return Header.OK(result);
    }
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s).map(
                user -> {return new UserDetailsImpl(user);}
        ).orElseThrow(UserNotFoundException::new);
    }

    private UserApiResponse response(User request){
        return UserApiResponse.builder()
                .nickname(request.getNickname())
                .username(request.getUsername())
                .area(request.getArea())
                .email(request.getEmail())
                .build();

    }
//    private UserApiResponse response(User request, Optional<Authentication> getAuth){
//        UserApiResponse userApiResponse = UserApiResponse.builder()
//                .nickname(request.getNickname())
//                .username(request.getUsername())
//                .area(request.getArea())
//                .build();
//
//        User user = new User();
//        user.setUid(-1L);
//        user.setAuth(Auth.user);
//        User loggedInUser ;
//        if(getAuth.equals(Optional.empty())) loggedInUser = user;
//        else loggedInUser = userRepository.findByUsername(
//                getAuth.get().getName()).orElseGet(() -> user);
//        switch (loggedInUser.getAuth()) {
//            case admin, manager:
//                userApiResponse.setUid(request.getUid());
//                userApiResponse.setLastLoginDate(request.getLastLoginDate());
//                userApiResponse.setSignUpDate(request.getSignUpDate());
//                userApiResponse.setAuth(request.getAuth());
//            case user:
//                if(!request.getAuth().equals(Auth.user)){
//                    throw new UserNotFoundException();
//                }
//                if ((!request.equals(loggedInUser))
//                        &&loggedInUser.getAuth().equals(Auth.user)) break;
//                userApiResponse.setEmail(request.getEmail());
//        }
//        return userApiResponse;
//    }
}

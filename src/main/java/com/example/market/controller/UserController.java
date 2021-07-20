package com.example.market.controller;

import com.example.market.exception.UserNotFoundException;
import com.example.market.model.network.Header;
import com.example.market.model.network.request.UserApiRequest;
import com.example.market.model.network.response.ProductApiResponse;
import com.example.market.model.network.response.UserApiResponse;
import com.example.market.service.ProductService;
import com.example.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping("/signUp")
    public String create(){
        return "user_signUp";
    }

    @PostMapping("/signUp")
    public RedirectView create(UserApiRequest userApiRequest){
        if(!userService.isLoggedIn().getData()){
            userService.create(userApiRequest);
        }
        return new RedirectView("/user/login");
    }

    @GetMapping("/myInfo")
    public String myInfo(Model model,@RequestParam(value="page",required = false,defaultValue= "1")int page){
        Header header = userService.getSelfInfo();
        if(header.getResultCode().equals("OK")){
            UserApiResponse userApiResponse = (UserApiResponse) header.getData();
            model.addAttribute("user",userApiResponse);
            List<ProductApiResponse> productList = productService.getProductByUser(userApiResponse.getUsername()).getData();
            model.addAttribute("pageCount",productService.countPage(productList.size(),6).getData());
            List<ProductApiResponse> productApiResponseList = productService
                    .subListProduct(productList,page,6).getData();
            if(userService.isLoggedIn().getData()){
                productApiResponseList.forEach(p->{
                    p.setBookmarked(productService.isBookmarked(p.getPid()).getData());
                });
            }else {
                productApiResponseList.forEach(p->{
                    p.setBookmarked(false);
                });
            }
            model.addAttribute("products",productApiResponseList);
        }else {
            model.addAttribute("error_message",header.getDescription());
        }
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser().getData());
        }
        return "user_info";
    }

    @GetMapping("/myInfo/edit")
    public String editMyInfo(Model model){
        Header header = userService.getSelfInfo();
        if(header.getResultCode().equals("OK")){
            model.addAttribute("user",header.getData());
        }else {
            model.addAttribute("error_message",header.getDescription());
        }
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser().getData());
        }
        return "edit_info";
    }

    @PostMapping("/myInfo/edit")
    public String updateMyInfo(Model model, UserApiRequest userApiRequest){
        System.out.println(userApiRequest);
        if(userService.isLoggedIn().getData()){
            Header header = userService.update(userApiRequest);
            if(header.getResultCode().equals("OK")){
                model.addAttribute("username",userService.loggedInUser().getData());
                return "redirect:/user/myInfo";
            }else {
                model.addAttribute("error_message","error");
            }
        }else {
            model.addAttribute("error_message","no permission");
        }
        return "error";
    }
    @GetMapping("/{username}")
    public String getUserInfo(Model model, @PathVariable String username,
                      @RequestParam(value="page",required = false,defaultValue= "1")int page){
        Header header = userService.getUserInfo(username);
        if(header.getResultCode().equals("OK")){
            UserApiResponse userApiResponse = (UserApiResponse) header.getData();
            userApiResponse.setEmail(null);
            model.addAttribute("user",userApiResponse);
            List<ProductApiResponse> productList = productService.getProductByUser(userApiResponse.getUsername()).getData();
            model.addAttribute("pageCount",productService.countPage(productList.size(),6).getData());
            List<ProductApiResponse> productApiResponseList = productService
                    .subListProduct(productList,page,6).getData();
            if(userService.isLoggedIn().getData()){
                productApiResponseList.forEach(p->{
                    p.setBookmarked(productService.isBookmarked(p.getPid()).getData());
                });
            }else {
                productApiResponseList.forEach(p->{
                    p.setBookmarked(false);
                });
            }
            model.addAttribute("products",productApiResponseList);
        }else {
            model.addAttribute("error_message",header.getDescription());
            System.out.println(header.getDescription());
            return "error";
        }
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser().getData());
        }
        return "user_info";
    }

    @PostMapping("/checkUsername")
    @ResponseBody
    public Boolean checkUsername(HttpServletRequest request){
        return userService.isUsernameExist(request.getParameter("username")).getData();
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException, ServletException {
        Header header = userService.logout(request,response);
        if(header.getResultCode().equals("ERROR")){
            model.addAttribute("error_message",header.getDescription());
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String getLogin(Model model){
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser());
        }
        return "user_login";
    }

    @PostMapping("/login")
    public String login(UserApiRequest userApiRequest, Model model){
        System.out.println(userApiRequest);
        Header header = userService.login(userApiRequest);
        System.out.println(header);
        if(header.getResultCode().equals("OK")){
            model.addAttribute("auth",header.getData());
            return "redirect:/";
        }else {
            model.addAttribute("error_message",header.getDescription());
            return "user_login";
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Header userValidationError(ConstraintViolationException ex) {
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            message.append(violation.getMessage().concat(";"));
        }
        return Header.ERROR(String.valueOf(message));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotFoundException.class)
    public Header userNotFoundException(UserNotFoundException ex) {
        return Header.ERROR(ex.getMessage());
    }

}

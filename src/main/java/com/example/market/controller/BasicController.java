package com.example.market.controller;

import com.example.market.model.enumclass.Area;
import com.example.market.model.network.response.ProductApiResponse;
import com.example.market.service.ProductService;
import com.example.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;

@Controller
public class BasicController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String main(Model model){
        if(userService.isLoggedIn().getData()){
            model.addAttribute("username",userService.loggedInUser());
        }
        ArrayList<ProductApiResponse> productList = productService.getAll().getData();
        model.addAttribute("products",productList.subList(0,4));
        return "index";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public String userNotFoundException(Model model,Exception ex) {
        model.addAttribute("message",ex.getMessage());
        return "error";
    }
}

package com.example.market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class DemoController {

    @RequestMapping(value = "/demo", method= RequestMethod.GET)
    public String demo(Model model) {
        model.addAttribute("result", "A");
        return "demo";
    }
}
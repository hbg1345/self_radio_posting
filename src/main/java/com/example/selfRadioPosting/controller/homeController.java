package com.example.selfRadioPosting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class homeController {

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}

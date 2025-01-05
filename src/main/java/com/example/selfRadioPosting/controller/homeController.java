package com.example.selfRadioPosting.controller;

import com.example.selfRadioPosting.dto.ArticleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
public class homeController {
    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/post/{category}")
    public String post(@PathVariable String category, Model model) {
        model.addAttribute("category", category);
        return "/article/posting";
    }
}

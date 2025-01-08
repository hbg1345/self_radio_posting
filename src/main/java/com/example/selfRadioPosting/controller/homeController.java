package com.example.selfRadioPosting.controller;

import com.example.selfRadioPosting.dto.ArticleDto;
import com.example.selfRadioPosting.service.AudioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
public class homeController {
    @GetMapping("/")
    public String index() throws IOException {
        return "index";
    }

    @GetMapping("/post/{category}")
    public String post(@PathVariable String category, Model model) {
        model.addAttribute("category", category);
        return "/article/posting";
    }
}

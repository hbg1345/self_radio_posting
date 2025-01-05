package com.example.selfRadioPosting.controller;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.dto.ArticleDto;
import com.example.selfRadioPosting.service.ArticleService;
import com.example.selfRadioPosting.service.S3FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ArticleController {
    private final S3FileService s3FileService;
    private final ArticleService articleService;

    @Autowired
    ArticleController(S3FileService s3FileService, ArticleService articleService){
        this.s3FileService = s3FileService;
        this.articleService = articleService;
    }

    @GetMapping("/article/show/{id}")
    public String show(@PathVariable Long id, Model model){
        ArticleDto articleDto = articleService.show(id);
        model.addAttribute("article", articleDto);
        log.info(articleDto.toString());
        return "/article/show";
    }

    @GetMapping("/article/test/{category}")
    public String test(@PathVariable String category, Model model)
    {
        model.addAttribute("category", category);
        List<ArticleDto> articleList = articleService.findAllByCategory(category);
        model.addAttribute("articleList", articleList);
        return "/article/test";
    }

    @PostMapping("/submit/{category}")
    public ResponseEntity<Map<String, String>> submit(
            @PathVariable String category,
            @ModelAttribute ArticleDto articleDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        ArrayList<String> urls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String url = s3FileService.upload(image, "dir");
                    urls.add(url);
                }
            }
        }
        articleService.submit(articleDto, urls);
        Map<String, String> response = new HashMap<>();
        response.put("message", "글이 성공적으로 등록되었습니다!");
        response.put("redirectUrl", "/article/test/" + category);
        return ResponseEntity.ok(response);
    }
}

package com.example.selfRadioPosting.api;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Map;

@Slf4j
@RestController
public class ArticleApiController {
    private final ArticleService articleService;
    @Autowired
    ArticleApiController(ArticleService articleService){
        this.articleService = articleService;
    }

    @PatchMapping("/api/articles/recommend")
    public ResponseEntity<Article> recommend(@RequestBody Map<String, Long> body, HttpServletRequest request)
            throws UnknownHostException, ParseException {
        boolean success = articleService.recommend(body.get("id"), request);
        log.info(String.valueOf(success));
        if (success)
            return ResponseEntity.ok(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

package com.example.selfRadioPosting.dto;

import com.example.selfRadioPosting.Entity.Article;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleDto {
    private Long id;
    private String category;
    private String writer;
    private String title;
    private String content;
    private String videoUrl;
    private String audioUrl;
    private String created;
    private String password;
    private String ip;
    private Long views;
    private Long recommend;

    public static ArticleDto createDto(Article article){
        return new ArticleDto(article.getId(),
                article.getCategory(),
                article.getWriter(),
                article.getTitle(),
                article.getContent(),
                article.getVideoUrl(),
                article.getAudioUrl(),
                article.getCreated(),
                article.getPassword(),
                article.getIp(),
                article.getViews(),
                article.getRecommend());
    }
    public Article createEntity(){
        return new Article(
            id,
            category,
            writer,
            title,
            content,
            videoUrl,
            audioUrl,
            created,
            password,
            ip,
            views,
            recommend);
    }
}

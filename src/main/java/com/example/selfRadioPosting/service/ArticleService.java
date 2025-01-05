package com.example.selfRadioPosting.service;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.dto.ArticleDto;
import com.example.selfRadioPosting.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ArticleService {
    private ArticleRepository articleRepository;

    @Autowired
    ArticleService(ArticleRepository articleRepository){
        this.articleRepository = articleRepository;
    }

    private ArrayList<String> extractBase64Images(String content) {
        ArrayList<String> base64Images = new ArrayList<>();
        Pattern pattern = Pattern.compile("data:image/[^;]+;base64,[^\"]+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            base64Images.add(matcher.group());
        }
        return base64Images;
    }

    public void submit(ArticleDto articleDto, List<String> urls){
        ArrayList<String> base64Images = extractBase64Images(articleDto.getContent());
        String updatedContent = articleDto.getContent();
        if (urls.size() != base64Images.size()){
            throw new IllegalArgumentException("url이랑 저거랑 뭔가 잘 안됐나 봐요!!");
        }
        for(int i=0; i<urls.size(); i++){
            updatedContent = updatedContent.replace(base64Images.get(i), urls.get(i));
        }
        Article article = new Article(null, articleDto.getCategory(), articleDto.getWriter(), articleDto.getTitle(), updatedContent);
        Article created = articleRepository.save(article);
        log.info(created.toString());
    }

    public List<ArticleDto> findAllByCategory(String category) {
        List<Article> articleList = articleRepository.findAllByCategory(category);
        return articleList.stream().map(ArticleDto::createDto).toList();
    }

    public ArticleDto show(Long id) {
        Article article =  articleRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("이상발생"));
        return ArticleDto.createDto(article);
    }
}

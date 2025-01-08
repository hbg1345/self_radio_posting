package com.example.selfRadioPosting.service;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.dto.ArticleDto;
import com.example.selfRadioPosting.repository.ArticleRepository;
import com.example.selfRadioPosting.util.Pair;
import com.example.selfRadioPosting.util.SubtitleAdder;
import com.google.cloud.texttospeech.v1.AudioConfigOrBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class ArticleService {
    private ArticleRepository articleRepository;
    private AudioService audioService;

    @Autowired
    ArticleService(ArticleRepository articleRepository, AudioService audioService){
        this.audioService = audioService;
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

    private static void extractTextNodes2(Node node, ArrayList<String> texts,
                                         ArrayList<ArrayList<String>> textsList) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text().trim();
            if (!text.isEmpty()) {
                texts.add(text);
            }
        }

        if (node.nodeName().equals("img")){
            textsList.add((ArrayList<String>) texts.clone());
            texts.clear();
        }
        for (Node child : node.childNodes()) {
            extractTextNodes2(child, texts, textsList);
        }
    }

    private static void extractTextNodes(Node node, List<String> textNodes) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text().trim();
            if (!text.isEmpty()) {
                textNodes.add(text);
            }
        }
//        log.info(node.nodeName());
        for (Node child : node.childNodes()) {
            extractTextNodes(child, textNodes);
        }
    }

    public void submit(
            ArticleDto articleDto,
            List<String> urls,
            List<MultipartFile> images) throws IOException {
        ArrayList<String> base64Images = extractBase64Images(articleDto.getContent());
        String updatedContent = articleDto.getContent();
        Document document = Jsoup.parse(articleDto.getContent());

        if (urls.size() != base64Images.size()){
            throw new IllegalArgumentException("image가 백스페이스로 지워지지 않았습니다. (Java script 에러)");
        }

        for(int i=0; i<urls.size(); i++){
            updatedContent = updatedContent.replace(base64Images.get(i), urls.get(i));
        }

        // 텍스트 노드를 담을 리스트 생성
        ArrayList<String> texts = new ArrayList<>();
        ArrayList<ArrayList<String>> textsList = new ArrayList<>();
//        log.info(String.valueOf(document.body().childNodeSize()));

        extractTextNodes2(document.body(), texts, textsList);
        if (!texts.isEmpty()) {
            textsList.add((ArrayList<String>) texts.clone());
            texts.clear();
        }
        log.info("텍스트 리스트의 리스트: " + textsList.toString());
        ArrayList<ArrayList<String>> converted = SubtitleAdder.convertTextsList(textsList);
        log.info("바뀐 텍스트 리스트의 리스트: " + converted.toString());
        // body의 모든 자식 노드 순회

        ArrayList<String> flattened = new ArrayList<>();
        converted.forEach(strings-> Collections.addAll(flattened, strings.toArray(new String[0])));
        log.info("납작해진 텍스트 리스트의 리스트: " + flattened.toString());

        extractTextNodes(document.body(), flattened);
        ArrayList<Float> durations = audioService.stringToSpeech(flattened);
        log.info(articleDto.getContent());
        log.info("텍스트 리스트:" + texts.toString());
        log.info(durations.toString());

        Article article = new Article(
                null,
                articleDto.getCategory(),
                articleDto.getWriter(),
                articleDto.getTitle(),
                updatedContent);

        Article created = articleRepository.save(article);
        log.info(created.toString());
        ArrayList<BufferedImage> bufferedImages = new ArrayList<>();
        for(int i=0; i<texts.size(); i++){
            bufferedImages.add(SubtitleAdder.addSubtitleToImage(images.get(i), texts.get(i)));
        }
        SubtitleAdder.createVideoFromImages(bufferedImages, "outputvideo.mp4", 1);
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

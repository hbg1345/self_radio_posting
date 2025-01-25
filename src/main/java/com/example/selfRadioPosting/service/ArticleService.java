package com.example.selfRadioPosting.service;

import com.example.selfRadioPosting.Entity.Article;
import com.example.selfRadioPosting.dto.ArticleDto;
import com.example.selfRadioPosting.repository.ArticleRepository;
import com.example.selfRadioPosting.util.FileManager;
import com.example.selfRadioPosting.util.SubtitleAdder;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class ArticleService {
    private ArticleRepository articleRepository;
    private AudioService audioService;
    private S3FileService s3FileService;

    @Autowired
    ArticleService(ArticleRepository articleRepository,
                   AudioService audioService,
                   S3FileService s3FileService){
        this.audioService = audioService;
        this.articleRepository = articleRepository;
        this.s3FileService = s3FileService;
    }

    private static void extractTexts(Node node, LinkedList<String> texts,
                                         LinkedList<LinkedList<String>> textsList) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text().trim();
            if (!text.isEmpty()) {
                texts.add(text);
            }
        }

        if (node.nodeName().equals("img")){
            if (texts.isEmpty())
                texts.add(".");
            textsList.add((LinkedList<String>) texts.clone());
            texts.clear();
        }
        for (Node child : node.childNodes()) {
            extractTexts(child, texts, textsList);
        }
    }

    @Transactional
    public void update(Article target){
        Article article = articleRepository.findById(target.getId()).orElseThrow(IllegalArgumentException::new);
        log.info(article.getAudioUrl());
        s3FileService.deleteFile(article.getVideoUrl());
        s3FileService.deleteFile(article.getAudioUrl());

        Document document = Jsoup.parse(article.getContent());
        Elements elements = document.getElementsByTag("img");
        for(Element element: elements){
            s3FileService.deleteFile(element.absUrl("src"));
        }
        articleRepository.update(target.getId(), target.getContent(), target.getAudioUrl(), target.getVideoUrl());
    }

    @Transactional
    public void delete(Long id){
        Article article = articleRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        log.info(article.getAudioUrl());
        s3FileService.deleteFile(article.getVideoUrl());
        s3FileService.deleteFile(article.getAudioUrl());

        Document document = Jsoup.parse(article.getContent());
        Elements elements = document.getElementsByTag("img");
        for(Element element: elements){
            s3FileService.deleteFile(element.absUrl("src"));
        }
        articleRepository.deleteById(id);
    }

    @Transactional
    public void submit(
            ArticleDto articleDto,
            List<String> urls,
            ArrayList<BufferedImage> images) throws IOException {

        String audioOutPath = "output_audio.wav";
        String videoOutPath = "output_video.mp4";
        String updatedContent = articleDto.getContent();
        Document document = Jsoup.parse(articleDto.getContent());
        Elements elements = document.getElementsByTag("img");

        if (urls.size() != elements.size()){
            throw new IllegalArgumentException("이미지 개수를 잘못 카운트하였습니다.");
        }

        for(int i=0; i<urls.size(); i++){
            updatedContent = updatedContent.replace(elements.get(i).absUrl("src"), urls.get(i));
        }

        LinkedList<String> texts = new LinkedList<>();
        LinkedList<LinkedList<String>> textsList = new LinkedList<>();
        extractTexts(document.body(), texts, textsList);
        if (!texts.isEmpty()) {
            textsList.add((LinkedList<String>) texts.clone());
            texts.clear();
        }

        ArrayList<ArrayList<String>> converted = SubtitleAdder.convertTextsList(textsList);
        ArrayList<String> flattened = new ArrayList<>();
        converted.forEach(strings-> Collections.addAll(flattened, strings.toArray(new String[0])));
        ArrayList<Float> durations = audioService.stringToSpeech(flattened, audioOutPath);

        LinkedList<BufferedImage> bufferedImages = new LinkedList<>();
        int fps = 4;
        int imgIndex = -1;
        int txtCount = converted.get(0).size();

        for(int i=0; i<flattened.size(); i++){
            if (txtCount == i){
                imgIndex++;
                txtCount += converted.get(imgIndex+1).size();
            }
            float duration = durations.get(i);
            int numOfFrame = (int) Math.ceil(fps * duration);
            BufferedImage subtitleAdded;
            if (imgIndex == - 1)
                subtitleAdded = SubtitleAdder.addSubtitleToImage(null, flattened.get(i));
            else subtitleAdded = SubtitleAdder.addSubtitleToImage(images.get(imgIndex), flattened.get(i));

            for (int j = 0; j < numOfFrame; j++) {
                bufferedImages.add(subtitleAdded);
            }
        }

        for (int i = imgIndex+1; i < images.size(); i++) {
            BufferedImage subtitleAdded =
                    SubtitleAdder.addSubtitleToImage(images.get(i), null);
            for (int j = 0; j < fps; j++) {
                bufferedImages.add(subtitleAdded);
            }
        }

        SubtitleAdder.createVideoFromImages(bufferedImages,
                videoOutPath, 4);
        File video = new File(videoOutPath);
        File audio = new File(audioOutPath);

        String videoUrl = s3FileService.upload(video, "dir");
        String audioUrl = s3FileService.upload(audio, "dir");

        Article article = articleDto.createEntity();
        article.setContent(updatedContent);
        article.setVideoUrl(videoUrl);
        article.setAudioUrl(audioUrl);

        Article saved;
        if (articleDto.getId() != null) {
            update(article);
            saved = article;
        }
        else saved = articleRepository.save(article);
        log.info(saved.toString());
        //Delete temporary created files.

        for (int i = 0; i < flattened.size(); i++) {
            FileManager.deleteFile("output" + i + ".wav");
        }

        FileManager.deleteFile(audioOutPath);
        FileManager.deleteFile(videoOutPath);
    }

    public List<ArticleDto> findAllByCategory(String category) {
        List<Article> articleList = articleRepository.findAllByCategory(category);
        return articleList.stream().map(ArticleDto::createDto).toList();
    }

    @Transactional
    public ArticleDto show(Long id) {
        articleRepository.updateView(id);
        Article article =  articleRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("이상발생"));
        return ArticleDto.createDto(article);
    }

    @Transactional
    public void recommend(Long id) {
        articleRepository.recommend(id);
    }
}

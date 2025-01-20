package com.example.selfRadioPosting.controller;

import com.example.selfRadioPosting.dto.ArticleDto;
import com.example.selfRadioPosting.service.ArticleService;
import com.example.selfRadioPosting.service.S3FileService;
import com.example.selfRadioPosting.util.FileManager;
import com.example.selfRadioPosting.util.IPManger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static javax.imageio.ImageIO.read;

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

    @GetMapping("/article/edit/{id}")
    public String edit(@PathVariable Long id, Model model){
        ArticleDto articleDto = articleService.show(id);
        model.addAttribute("article", articleDto);
        log.info(articleDto.toString());
        return "/article/edit";
    }

    @GetMapping("/article/delete/{id}")
    public String delete(@PathVariable Long id) throws UnsupportedEncodingException {
        String category = articleService.show(id).getCategory();
        articleService.delete(id);
        return "redirect:/article/test/" + URLEncoder.encode(category, StandardCharsets.UTF_8.toString());
    }

    @GetMapping("/article/recommend/{id}")
    public String recommend(@PathVariable Long id) {
        articleService.recommend(id);
        return "redirect:/article/show/" + id;
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
    @Transactional
    public ResponseEntity<Map<String, String>> submit(
            @PathVariable String category,
            @ModelAttribute ArticleDto articleDto,
            HttpServletRequest request) throws IOException {

        String ipAddress = IPManger.getClientIp(request);
        articleDto.setIp(ipAddress);

        ArrayList<String> urls = new ArrayList<>();
        Document document = Jsoup.parse(articleDto.getContent());

        Elements elements = document.getElementsByTag("img");

        ArrayList<BufferedImage> images = new ArrayList<>();
        List<File> fileImages = new LinkedList<>();
        List<String> tempFilenames = new LinkedList<>();

        for (int i = 0; i < elements.size(); i++) {
            String imageDirPath = "image" + images.size();
            Element element = elements.get(i);
            if (element.hasAttr("data-file-name")){
                String base64Image = element.absUrl("src");
                imageDirPath += "."+"png";

                byte[] image = Base64.decodeBase64(new String(
                        base64Image.substring(
                                base64Image.indexOf(",") + 1)).getBytes("UTF-8"));

                try (FileOutputStream stream = new FileOutputStream(imageDirPath)) {
                    stream.write(image);
                }
                catch (Exception e){
                    log.info(e.getMessage());
                }
                images.add(read(new File(imageDirPath)));
            }
            else {
                String surl = element.absUrl("src");
                URL url = new URL(surl);
                images.add(read(url));
                imageDirPath += "." + "png";
                try(InputStream in = url.openStream();
                   FileOutputStream out= new FileOutputStream(imageDirPath))
                {
                   while(true) {
                        int data = in.read();
                        if (data == -1) break;
                        out.write(data);
                    }
                }
                catch (Exception e){
                    log.info(e.getMessage());
                }
            }

            fileImages.add(new File(imageDirPath));
            tempFilenames.add(imageDirPath);
        }

        for (File image : fileImages) {
            String url = s3FileService.upload(image, "dir");
            urls.add(url);
        }
        articleService.submit(articleDto, urls, images);

        for (String tempFilename: tempFilenames) {
            FileManager.deleteFile(tempFilename);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "글이 성공적으로 등록되었습니다!");
        response.put("redirectUrl", "/article/test/" + category);
        return ResponseEntity.ok(response);
    }
}

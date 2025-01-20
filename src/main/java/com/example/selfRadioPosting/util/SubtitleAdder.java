package com.example.selfRadioPosting.util;

import lombok.extern.slf4j.Slf4j;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static javax.imageio.ImageIO.read;

@Slf4j
public class SubtitleAdder {
    private static final int targetHeight = 600;
    private static final int targetWidth = 600;
    private static final Font font = new Font("맑은 고딕", Font.BOLD, 30);
    private static final Color fontColor = Color.WHITE;

    static ArrayList<String> splitString(String inputString){
        ArrayList<String> strings = new ArrayList<>();
        BufferedImage notUsedImage = new BufferedImage(
                targetWidth, targetHeight, 1);
        Graphics2D g2d = notUsedImage.createGraphics();
        FontMetrics fontMetrics = g2d.getFontMetrics(font);
        int start = 0;
        while (start < inputString.length()) {
            int low = start;
            int high = inputString.length();
            while (high - low > 1) {
                int mid = (high + low) / 2;
                if (fontMetrics.stringWidth(inputString.substring(start, mid + 1)) > targetWidth)
                    high = mid;
                else low = mid;
            }
            strings.add(inputString.substring(start, low+1));
            start = low + 1;
        }
//        log.info("분할된 문자열들: " + strings.toString());
        return strings;
    }

    public static ArrayList<String> splitTexts(List<String> texts){
        ArrayList<String> ret = new ArrayList<>();
        for(String text: texts){
            for(String splitted: splitString(text)){
                ret.add(splitted);
            }
        }
        return ret;
    }

    public static ArrayList<ArrayList<String>> convertTextsList(LinkedList<LinkedList<String>> textsList){
        ArrayList<ArrayList<String>> ret = new ArrayList<>();
        for(LinkedList<String> texts: textsList){
            ret.add(splitTexts(texts));
        }
        return ret;
    }

    public static BufferedImage addSubtitleToImage(
            BufferedImage file, String subtitle) throws IOException{
        BufferedImage image =  new BufferedImage(
                targetWidth, targetHeight, 1);

        if (file != null) {
            image = file;
        }

        BufferedImage resizedImage = new BufferedImage(
                targetWidth, targetHeight, image.getType());
        int height = image.getHeight();
        int width = image.getWidth();
        int padX, padY;

        if (height >= width){
            width = (int) (width * (targetHeight / (float) height));
            height = targetHeight;
        }

        else {
            height = (int)(height * (targetWidth / (float) width));
            width = targetWidth;
        }

        log.info(String.valueOf(height));
        log.info(String.valueOf(width));

        padX = (targetWidth - width) / 2;
        padY = (targetHeight - height) / 2;

        Graphics2D re_g2d = resizedImage.createGraphics();

        re_g2d.drawImage(image, padX, padY, width, height, null);
        re_g2d.dispose();
        image = resizedImage;
        // 2. Graphics2D로 텍스트 추가
        Graphics2D g2d = image.createGraphics();
        if (subtitle != null) {
            g2d.setFont(font);
            g2d.setColor(fontColor);
            g2d.getFontMetrics().stringWidth(subtitle);
            // 텍스트 위치 설정 (하단 중앙)
            int textWidth = g2d.getFontMetrics().stringWidth(subtitle);
            int x = (image.getWidth() - textWidth) / 2;
            int y = image.getHeight() - 50; // 하단에서 50px 위
            g2d.drawString(subtitle, x, y);
        }
        g2d.dispose(); // Graphics2D 리소스 해제
        // 3. 수정된 BufferedImage 반환
        return image;
    }

    public static void createVideoFromImages(
            List<BufferedImage> images,
            String outputFilePath,
            int frameRate) throws IOException {
        // 1. 동영상 파일 생성
        File outputFile = new File(outputFilePath);
        // 2. AWTSequenceEncoder 생성
        AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(outputFile, frameRate);

        // 3. BufferedImage를 프레임으로 추가
        for (BufferedImage image : images) {
            encoder.encodeImage(image);
        }
        // 4. 인코딩 완료
        encoder.finish();
        log.info("동영상이 생성되었습니다: " + outputFilePath);
    }
    public static void main(String [] args) throws IOException {
        BufferedImage temp = read(new File("image0.png"));
        if (temp == null){
            log.info("?000");
        }
        BufferedImage temp2 = read(new File("image1.png"));
        if (temp2 == null){
            log.info("?111");
        }
    }
}

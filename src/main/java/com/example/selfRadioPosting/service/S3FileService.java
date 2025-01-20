package com.example.selfRadioPosting.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class S3FileService {
    private final AmazonS3 amazonS3;
    private final String bucket;

    public S3FileService(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucket) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
    }

    public String upload(File file, String dirName) throws IOException {
        String uniqueFileName = makeUniqueFileName(file.getName());
        String fileName = dirName + "/" + uniqueFileName;
        String uploadImageUrl = putS3(file, fileName);
//        freeGarbageFile(file);
        log.info(uploadImageUrl+"에 파일 업로드 완료");
        return uploadImageUrl;
    }

    public String upload(MultipartFile file, String dirName) throws IOException {
        String uniqueFileName = makeUniqueFileName(file.getOriginalFilename());
        String fileName = dirName + "/" + uniqueFileName;
        File uploadFile = convert(file, uniqueFileName);
        String uploadImageUrl = putS3(uploadFile, fileName);
        freeGarbageFile(uploadFile);
        log.info(uploadImageUrl+"에 파일 업로드 완료");
        return uploadImageUrl;
    }

    private String makeUniqueFileName(String fileName){
        log.info("ori name is " + fileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "_" + fileName.replaceAll("\\s", "_");
    }

    private File convert(MultipartFile file, String uniqueFileName) throws IOException {
        File convertedFile = new File(uniqueFileName);
        if (convertedFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                fos.write(file.getBytes());
            }
            catch (IOException e) {
                log.error("MultipartFile -> File 과정에서 에외 발생: {}", e.getMessage());
                throw e;
            }
            return convertedFile;
        }
        throw new IllegalArgumentException(String.format(file.getOriginalFilename()+
                "의 변환 과정에서 문제 발생"));
    }

    private String putS3(File uploadFile, String fileName) {
        log.info("file name is " + fileName);
        log.info("file name is " + uploadFile.getName());

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void freeGarbageFile(File targetFile) {
        try {
            targetFile.delete();
        }
        catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        try {
            // URL 디코딩을 통해 원래의 파일 이름을 가져옵니다.
            String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
            amazonS3.deleteObject(bucket, decodedFileName);
            log.info("S3 {} 파일 삭제 성공 ", decodedFileName);
        } catch (UnsupportedEncodingException e) {
            log.error("S3 파일 삭제 실패" + e.getMessage());
        }
    }

    public String updateFile(MultipartFile newFile, String oldFileName, String dirName) throws IOException {
        // 기존 파일 삭제
        log.info("기존 {} 파일 삭제", oldFileName);
        deleteFile(oldFileName);
        // 새 파일 업로드
        return upload(newFile, dirName);
    }
}

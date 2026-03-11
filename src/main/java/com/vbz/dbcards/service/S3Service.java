//```java
//package com.vbz.dbcards.service;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//@Service
//public class S3Service {
//
//    private final S3Client s3Client;
//
//    @Value("${aws.bucket.name}")
//    private String bucketName;
//
//    public S3Service(S3Client s3Client) {
//        this.s3Client = s3Client;
//    }
//
//    public String uploadFile(MultipartFile file) throws IOException {
//
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//
//        PutObjectRequest request =
//                PutObjectRequest.builder()
//                        .bucket(bucketName)
//                        .key(fileName)
//                        .contentType(file.getContentType())
//                        .build();
//
//        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
//
//        return "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + fileName;
//    }
//}
//```

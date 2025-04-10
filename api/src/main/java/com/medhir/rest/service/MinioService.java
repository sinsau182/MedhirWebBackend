package com.medhir.rest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioService {


    @Value("${minio.serviceUrl}")
    private String minioserviceUrl;

    @Value("${minio.attendanceBucketName}")
    private String attendanceBucketName;

    @Value("${minio.documentBucketName}")
    private String documentBucketName;

    RestTemplate restTemplate = new RestTemplate();

    public String generateUUID() {
        return restTemplate.getForObject(minioserviceUrl + "/generate-uuid", String.class);
    }

    public String uploadFile(String bucketName, MultipartFile file,String employeeId) {

        String uniqueId = generateUUID(); // Fetch UUID from MinIO Service
        String filePath = employeeId + "/" + uniqueId + "_" + file.getOriginalFilename(); // Construct path

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("bucketName", bucketName);
        body.add("file", file.getResource());
        body.add("filePath", filePath);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(minioserviceUrl + "/upload", requestEntity, String.class);

        return response.getBody();  // MinIO file URL
    }


    public String uploadProfileImage(MultipartFile file,String employeeId){
        return uploadFile(attendanceBucketName,file,employeeId);
    }

    public String uploadDocumentsImg(MultipartFile file, String employeeId){
        return uploadFile(documentBucketName,file,employeeId);
    }
}

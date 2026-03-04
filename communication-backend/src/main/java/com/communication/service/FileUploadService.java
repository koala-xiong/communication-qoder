package com.communication.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String uploadFile(MultipartFile file, String subDirectory);

    void deleteFile(String fileUrl);

    boolean isValidImageType(String contentType);

    boolean isValidVideoType(String contentType);
}

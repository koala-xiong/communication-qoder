package com.communication.controller;

import com.communication.dto.ApiResponse;
import com.communication.entity.MediaType;
import com.communication.service.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final FileUploadService fileUploadService;

    public UploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        String contentType = file.getContentType();
        if (!fileUploadService.isValidImageType(contentType)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid image type. Allowed: JPEG, PNG, GIF, WebP"));
        }

        String url = fileUploadService.uploadFile(file, "images");

        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("mediaType", MediaType.IMAGE);

        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", result));
    }

    @PostMapping("/video")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadVideo(
            @RequestParam("file") MultipartFile file) {
        String contentType = file.getContentType();
        if (!fileUploadService.isValidVideoType(contentType)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid video type. Allowed: MP4, WebM, MOV"));
        }

        String url = fileUploadService.uploadFile(file, "videos");

        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("mediaType", MediaType.VIDEO);

        return ResponseEntity.ok(ApiResponse.success("Video uploaded successfully", result));
    }
}

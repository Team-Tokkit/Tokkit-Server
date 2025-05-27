package com.example.Tokkit_server.global.controller;

import com.example.Tokkit_server.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("images/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        byte[] imageBytes = s3Service.getImageBytes(fileName);

        MediaType mediaType = getMediaType(fileName);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }

    private MediaType getMediaType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (ext) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "webp" -> MediaType.valueOf("image/webp");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
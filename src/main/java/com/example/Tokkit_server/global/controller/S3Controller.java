package com.example.Tokkit_server.global.controller;

import com.example.Tokkit_server.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/upload-url")
    public ResponseEntity<String> getUploadUrl(@RequestParam String fileName) {
        return ResponseEntity.ok(s3Service.generateUploadUrl(fileName));
    }
}

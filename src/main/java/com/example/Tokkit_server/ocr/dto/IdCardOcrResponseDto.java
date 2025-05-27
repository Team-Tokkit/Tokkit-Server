package com.example.Tokkit_server.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdCardOcrResponseDto {
    private String name;
    private String rrnFront;
    private String issuedDate;
}

package com.example.Tokkit_server.dto.response;

import java.time.LocalDateTime;

import com.example.Tokkit_server.Enum.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionDetailResponse {
    private Long id;
    private TransactionType type;
    private Long amount;
    private String description;
    private LocalDateTime createdAt;
}

package com.example.Tokkit_server.wallet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AutoConvertSettingRequest {
    private boolean enabled;
    private int dayOfMonth;
    private int hour;
    private int minute;
    private long amount;
}
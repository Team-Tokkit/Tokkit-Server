package com.example.Tokkit_server.wallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AutoConvertSettingResponse {
    private boolean enabled;
    private int dayOfMonth;
    private int hour;
    private int minute;
    private long amount;
}
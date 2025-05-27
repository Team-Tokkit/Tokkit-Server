package com.example.Tokkit_server.store.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum StoreCategory {
    FOOD("음식점"),
    MEDICAL("의료"),
    SERVICE("서비스"),
    TOURISM("관광"),
    LODGING("숙박"),
    EDUCATION("교육");

    private final String koreanName;

    StoreCategory(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

}



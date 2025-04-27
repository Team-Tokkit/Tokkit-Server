package com.example.Tokkit_server.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Merchant {

    // 가맹점주 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대표자명
    private String name;

    // 사업자 번호
    private String phoneNumber;

    // 이메일
    private String email;

    // 전화번호
    private String phone;

    // 상세주소
    private String address_detail;

    // 휴면 상태
    private String status;

    // 알림 설정
    private String alert_setting;

    // 지역코드
    private String code;

}

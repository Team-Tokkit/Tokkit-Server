package com.example.Tokkit_server.email_validation.entity;

import com.example.Tokkit_server.email_validation.enums.EmailPurpose;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailValidation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;

    @Enumerated(EnumType.STRING)
    private EmailPurpose purpose;

    private String ePw;
    private Date exp;
    private Boolean isVerified;
}


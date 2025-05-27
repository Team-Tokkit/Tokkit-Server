package com.example.Tokkit_server.user.entity;

import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "email_validation")
public class EmailValidation extends BaseTimeEntity {
    @Id
    private String email;

    private String ePw;
    private Date exp;
    private Boolean isVerified;
}


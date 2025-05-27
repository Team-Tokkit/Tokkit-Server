package com.example.Tokkit_server.merchant.entity;

import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MerchantSimplePasswordResetEmailValidation extends BaseTimeEntity {
    @Id
    private String email;

    private String code;

    private Date exp;

    private Boolean isVerified;

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
}

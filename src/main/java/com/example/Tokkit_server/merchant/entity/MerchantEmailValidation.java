package com.example.Tokkit_server.merchant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "merchant_email_validation")
public class MerchantEmailValidation {
    @Id
    private String email;

    private String ePw;
    private Date exp;
    private Boolean isVerified;
}

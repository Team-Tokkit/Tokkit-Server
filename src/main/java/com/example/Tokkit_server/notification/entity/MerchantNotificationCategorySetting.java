package com.example.Tokkit_server.notification.entity;

import com.example.Tokkit_server.global.entity.BaseTimeEntity;
import com.example.Tokkit_server.merchant.entity.Merchant;
import com.example.Tokkit_server.notification.enums.NotificationCategory;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MerchantNotificationCategorySetting extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationCategory category;

    @Column(nullable = false)
    private boolean enabled;

    public void update(boolean enabled) {
        this.enabled = enabled;
    }
}

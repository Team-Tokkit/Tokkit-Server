package com.example.Tokkit_server.user.entity;

import com.example.Tokkit_server.user.dto.request.UpdateUserPasswordRequestDto;
import com.example.Tokkit_server.wallet.entity.Wallet;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    @Column(nullable = false)
    private String simplePassword;

    private Boolean isDormant;

    private String roles;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Wallet wallet;

    // Dto를 사용해서 update하는 메서드.
    // Entity에는 Setter를 사용하지 않는 것이 좋음
    public void update(UpdateUserPasswordRequestDto userRequestDto) {
        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            this.password = userRequestDto.getPassword();
        }
    }

    public User encodePassword(PasswordEncoder passwordEncoder){
        password = passwordEncoder.encode(password);
        return this;
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword){
        return passwordEncoder.matches(checkPassword, getPassword());
    }


    public void updateSimplePassword(String newSimplePassword) {
        this.simplePassword = newSimplePassword;
    }

    public void updateNameAndPhoneNumber(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
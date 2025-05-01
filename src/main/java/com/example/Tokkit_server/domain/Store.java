package com.example.Tokkit_server.domain;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(nullable = false)
	private String name; // 매장이름i

	@Column(nullable = false)
	private StoreCategory storeCategory;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", unique = true)
	private Merchant merchant;

	@Column(nullable = false)
	private String adress; // 지역

}

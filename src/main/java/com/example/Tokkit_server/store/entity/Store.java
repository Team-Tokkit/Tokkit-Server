package com.example.Tokkit_server.store.entity;

import com.example.Tokkit_server.region.entity.Region;
import com.example.Tokkit_server.store_category.entity.StoreCategory;
import com.example.Tokkit_server.voucher.entity.Voucher;
import com.example.Tokkit_server.global.entity.BaseTimeEntity;

import com.example.Tokkit_server.merchant.entity.Merchant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String storeName;

	private String roadAddress;

	private String newZipcode;

	private Double longitude;

	private Double latitude;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_category_id")
	private StoreCategory storeCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id")
	private Region region;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", unique = true)
	private Merchant merchant;


	@Column(columnDefinition = "POINT")
	private Point location;

}

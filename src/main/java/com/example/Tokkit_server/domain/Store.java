package com.example.Tokkit_server.domain;

import com.example.Tokkit_server.Enum.StoreCategory;
import com.example.Tokkit_server.domain.common.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(nullable = false)
	private String name; // 매장이름i

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StoreCategory storeCategory;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", unique = true)
	private Merchant merchant;

	@Column(nullable = false)
	private String address;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "voucher_store",
			joinColumns = @JoinColumn(name = "store_id"),
			inverseJoinColumns = @JoinColumn(name = "voucher_id")
	)
	private List<Voucher> vouchers = new ArrayList<>();

}

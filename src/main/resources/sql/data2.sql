
-- 사용자 및 지갑
INSERT INTO user (id, name, email, password, simple_password, is_dormant, roles)
VALUES (1, '테스트유저', 'test@example.com', 'pwd1234', '1234', false, 'USER');

INSERT INTO wallet (id, user_id, account_number, deposit_balance, token_balance, wallet_type)
VALUES (1, 1, '111-222', 0, 30000, 'USER');

-- 가맹점 및 매장
INSERT INTO merchant (id, name, phone_number, email, password, simple_password, business_number, is_dormant, roles)
VALUES (1, '투썸플레이스', '010-0000-0000', 'merchant@store.com', 'pw1234', '1234', '123-45-67890', false, 'MERCHANT');

INSERT INTO region (id, sido_code, sido_name, sigungu_code, sigungu_name)
VALUES (1, 11, '서울특별시', 11680, '강남구');

INSERT INTO store (id, store_name, road_address, new_zipcode, longitude, latitude, store_category, region_id, merchant_id, location)
VALUES (100, '투썸플레이스 선릉점', '서울 강남구 강남대로 100', '12345', 127.030204, 37.4995903, 'FOOD', 1, 1, ST_GeomFromText('POINT(127.030204 37.4995903)'));

-- 바우처 A (정상) & B (만료)
INSERT INTO voucher (
    id, name, description, detail_description, price, original_price,
    total_count, remaining_count, valid_date, refund_policy,
    contact, merchant_id, store_category, created_at, updated_at
)
VALUES (
           10, '청년 식사 바우처', '설명1', '자세한 설명입니다', 60000, 70000,
           100, 100, '2025-06-13 07:59:39', '환불 불가',
           '010-1111-1111', 1, 'FOOD', NOW(), NOW()
       ),
       (
           11, '의료 지원 바우처', '설명2', '자세한 설명입니다', 50000, 60000,
           100, 100, '2024-05-14 07:59:39', '환불 불가',
           '010-2222-2222', 1, 'MEDICAL', NOW(), NOW()
       );

-- 바우처 매장 연결 (A만 가능)
INSERT INTO voucher_store (voucher_id, store_id)
VALUES (10, 100);

-- 바우처 소유권
INSERT INTO voucher_ownership (id, voucher_id, wallet_id, remaining_amount, status, created_at, updated_at)
VALUES
    (101, 10, 1, 50000, 'AVAILABLE', NOW(), NOW()),
    (102, 11, 1, 50000, 'AVAILABLE', NOW(), NOW());

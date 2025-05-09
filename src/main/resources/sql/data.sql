-- User 더미 데이터
INSERT INTO user (id, name, email, password, phone_number, simple_password, is_dormant, roles, created_at, updated_at) VALUES
                                                                                                                           (1, '홍길동', 'user1@example.com', 'password1', '010-1234-5678', '1234', false, 'USER', NOW(), NOW()),
                                                                                                                           (2, '김철수', 'user2@example.com', 'password2', '010-9876-5432', '5678', false, 'USER', NOW(), NOW());

-- Merchant 더미 데이터
INSERT INTO merchant (id, name, phone_number, email, password, simple_password, business_number, is_dormant, roles, created_at, updated_at) VALUES
                                                                                                                                                (1, '상점주1', '010-1111-2222', 'merchant1@example.com', 'password1', '1111', '123-45-67890', false, 'MERCHANT', NOW(), NOW()),
                                                                                                                                                (2, '상점주2', '010-3333-4444', 'merchant2@example.com', 'password2', '2222', '987-65-43210', false, 'MERCHANT', NOW(), NOW());

-- Wallet 더미 데이터
INSERT INTO wallet (id, user_id, merchant_id, deposit_balance, token_balance, wallet_type, created_at, updated_at) VALUES
                                                                                                                       (1, 1, NULL, 100000, 500, 'USER', NOW(), NOW()),
                                                                                                                       (2, NULL, 1, 200000, 1000, 'MERCHANT', NOW(), NOW());

-- StoreCategory 더미 데이터
INSERT INTO store_category (id, code, name) VALUES
                                                (1, 'CULTURE', '문화'),
                                                (2, 'JOB', '직업');

-- Region 더미 데이터
INSERT INTO region (id, sido_code, sido_name, sigungu_code, sigungu_name) VALUES
                                                                              (1, 11, '서울특별시', 110, '강남구'),
                                                                              (2, 11, '서울특별시', 120, '홍대');

-- Store 더미 데이터
INSERT INTO store (id, store_name, road_address, new_zipcode, longitude, latitude, store_category_id, region_id, merchant_id, location, created_at, updated_at) VALUES
                                                                                                                                                                    (1, '스타벅스 강남점', '서울 강남구 테헤란로', '06241', 127.0276, 37.4979, 1, 1, 1, ST_GeomFromText('POINT(127.0276 37.4979)'), NOW(), NOW()),
                                                                                                                                                                    (2, '이디야 홍대점', '서울 마포구 양화로', '04044', 126.9236, 37.5560, 1, 2, 2, ST_GeomFromText('POINT(126.9236 37.5560)'), NOW(), NOW());

-- Voucher 더미 데이터
INSERT INTO voucher (id, name, description, detail_description, price, valid_date, refund_policy, contact, merchant_id, category_id, created_at, updated_at) VALUES
                                                                                                                                                                 (1, '할인권', '10% 할인', '모든 매장에서 사용 가능', 10000, '2024-12-31 23:59:59', '환불 불가', '010-1234-5678', 1, 1, NOW(), NOW()),
                                                                                                                                                                 (2, '무료 음료권', '1잔 무료', '스타벅스에서 사용 가능', 5000, '2024-12-31 23:59:59', '환불 불가', '010-9876-5432', 2, 1, NOW(), NOW());

-- VoucherOwnership 더미 데이터
INSERT INTO voucher_ownership (id, voucher_id, wallet_id, remaining_amount, status, created_at, updated_at) VALUES
                                                                                                                (1, 1, 1, 10000, 'AVAILABLE', NOW(), NOW()),
                                                                                                                (2, 2, 1, 5000, 'AVAILABLE', NOW(), NOW());

-- VoucherStore 더미 데이터
INSERT INTO voucher_store (id, voucher_id, store_id, created_at, updated_at) VALUES
                                                                                 (1, 1, 1, NOW(), NOW()),
                                                                                 (2, 2, 2, NOW(), NOW());
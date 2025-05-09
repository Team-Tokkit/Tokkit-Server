-- Merchant 더미 데이터
INSERT INTO merchant (id, created_at, updated_at, business_number, email, is_dormant, name, password, phone_number, roles, simple_password) VALUES
                                                                                                                                                (1, NOW(), NOW(), '123-45-67890', 'merchant1@example.com', 0, '상점주1', 'password1', '010-1111-2222', 'MERCHANT', '1111'),
                                                                                                                                                (2, NOW(), NOW(), '987-65-43210', 'merchant2@example.com', 0, '상점주2', 'password2', '010-3333-4444', 'MERCHANT', '2222');

-- Notice 더미 데이터
INSERT INTO notice (id, is_deleted, created_at, updated_at, content, title) VALUES
                                                                                (1, 0, NOW(), NOW(), '공지사항 내용1', '공지사항 제목1'),
                                                                                (2, 0, NOW(), NOW(), '공지사항 내용2', '공지사항 제목2');

-- Region 더미 데이터
INSERT INTO region (id, sido_code, sigungu_code, sido_name, sigungu_name) VALUES
                                                                              (1, 11, 110, '서울특별시', '강남구'),
                                                                              (2, 11, 120, '서울특별시', '홍대');

-- SimplePasswordResetEmailValidation 더미 데이터
INSERT INTO simple_password_reset_email_validation (id, is_verified, created_at, updated_at, exp, code, email) VALUES
                                                                                                                   (1, 1, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), '123456', 'user1@example.com'),
                                                                                                                   (2, 0, NOW(), NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), '654321', 'user2@example.com');

-- Store 더미 데이터
INSERT INTO store (id, latitude, longitude, created_at, updated_at, merchant_id, region_id, store_category, new_zipcode, road_address, store_name, location) VALUES
                                                                                                                                                                 (1, 37.4979, 127.0276, NOW(), NOW(), 1, 1, 'FOOD', '06241', '서울 강남구 테헤란로', '스타벅스 강남점', ST_GeomFromText('POINT(127.0276 37.4979)')),
                                                                                                                                                                 (2, 37.5560, 126.9236, NOW(), NOW(), 2, 2, 'FOOD', '04044', '서울 마포구 양화로', '이디야 홍대점', ST_GeomFromText('POINT(126.9236 37.5560)'));

-- Token 더미 데이터
INSERT INTO token (id, created_at, updated_at, email, token) VALUES
                                                                 (1, NOW(), NOW(), 'user1@example.com', 'token123'),
                                                                 (2, NOW(), NOW(), 'user2@example.com', 'token456');

-- User 더미 데이터
INSERT INTO user (id, is_dormant, created_at, updated_at, email, name, password, phone_number, roles, simple_password) VALUES
                                                                                                                           (1, 0, NOW(), NOW(), 'user1@example.com', '홍길동', 'password1', '010-1234-5678', 'USER', '1234'),
                                                                                                                           (2, 0, NOW(), NOW(), 'user2@example.com', '김철수', 'password2', '010-9876-5432', 'USER', '5678');

-- Notification 더미 데이터
INSERT INTO notification (id, deleted, sent, created_at, updated_at, user_id, category, content, title) VALUES
                                                                                                            (1, 0, 1, NOW(), NOW(), 1, 'PAYMENT', '결제 알림 내용1', '결제 알림 제목1'),
                                                                                                            (2, 0, 1, NOW(), NOW(), 2, 'SYSTEM', '시스템 알림 내용2', '시스템 알림 제목2');

-- NotificationCategorySetting 더미 데이터
INSERT INTO notification_category_setting (id, enabled, created_at, updated_at, user_id, category) VALUES
                                                                                                       (1, 1, NOW(), NOW(), 1, 'PAYMENT'),
                                                                                                       (2, 1, NOW(), NOW(), 2, 'SYSTEM');

-- Voucher 더미 데이터
INSERT INTO voucher (id, original_price, price, remaining_count, total_count, created_at, updated_at, merchant_id, valid_date, contact, description, detail_description, name, refund_policy, store_category) VALUES
                                                                                                                                                                                                                  (1, 15000, 10000, 100, 100, NOW(), NOW(), 1, DATE_ADD(NOW(), INTERVAL 30 DAY), '010-1234-5678', '할인권 설명1', '상세 설명1', '할인권1', '환불 불가', 'FOOD'),
                                                                                                                                                                                                                  (2, 20000, 15000, 50, 50, NOW(), NOW(), 2, DATE_ADD(NOW(), INTERVAL 60 DAY), '010-9876-5432', '할인권 설명2', '상세 설명2', '할인권2', '환불 불가', 'SERVICE');-- VoucherImage 더미 데이터
INSERT INTO voucher_image (id, created_at, updated_at, image_url) VALUES
                                                                      (1, NOW(), NOW(), 'https://example.com/image1.jpg'),
                                                                      (2, NOW(), NOW(), 'https://example.com/image2.jpg');

-- VoucherStore 더미 데이터
INSERT INTO voucher_store (id, created_at, updated_at, store_id, voucher_id) VALUES
                                                                                 (1, NOW(), NOW(), 1, 1),
                                                                                 (2, NOW(), NOW(), 2, 2);

-- Wallet 더미 데이터
INSERT INTO wallet (id, created_at, updated_at, deposit_balance, token_balance, merchant_id, user_id, account_number, wallet_type) VALUES
                                                                                                                                       (1, NOW(), NOW(), 100000, 500, NULL, 1, '1234567890', 'USER'),
                                                                                                                                       (2, NOW(), NOW(), 200000, 1000, 1, NULL, '0987654321', 'MERCHANT');

-- Transaction 더미 데이터
INSERT INTO transaction (id, amount, created_at, updated_at, wallet_id, description, tx_hash, type) VALUES
                                                                                                        (1, 50000, NOW(), NOW(), 1, '충전', 'tx123', 'DEPOSIT'),
                                                                                                        (2, 10000, NOW(), NOW(), 2, '구매', 'tx456', 'PURCHASE');

-- VoucherOwnership 더미 데이터
INSERT INTO voucher_ownership (id, created_at, updated_at, voucher_id, wallet_id, remaining_amount, status) VALUES
                                                                                                                (1, NOW(), NOW(), 1, 1, 10000, 'AVAILABLE'),
                                                                                                                (2, NOW(), NOW(), 2, 2, 5000, 'AVAILABLE');
INSERT INTO wallet.member (id,name, email, password, deposit_account, withdrawal_account, created_at, updated_at)
VALUES (1,'test', 'test@test.com', '$2a$10$cpVMtB5hjG2VBVMQh/gs1eGf.vtQBbA1MF2kjyDhtkueeMi5A9RR6', '12345678', '87654321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO wallet.wallet (member_id , balance)
VALUES (1, 0.0);

INSERT INTO wallet.product(id, name, price, product_type)
VALUES (1, '5000원 쿠폰', 5000.0, 'COUPON');

INSERT INTO wallet.product(id, name, price, product_type)
VALUES (2,'10000원 쿠폰', 10000.0, 'COUPON');
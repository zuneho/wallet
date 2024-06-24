CREATE SCHEMA IF NOT EXISTS wallet;

-- member 테이블 생성
CREATE TABLE wallet.member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '회원 ID',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '회원 이메일',
    name VARCHAR(255) NOT NULL COMMENT '회원 이름',
    password VARCHAR(255) NOT NULL COMMENT '회원 비밀번호',
    deposit_account VARCHAR(255) NOT NULL COMMENT '입금 가상 계좌',
    withdrawal_account VARCHAR(255) NOT NULL COMMENT '출금 계좌',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간'
);

-- wallet 테이블 생성
CREATE TABLE wallet.wallet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '월렛 ID',
    member_id BIGINT NOT NULL UNIQUE COMMENT '회원 ID',
    balance DOUBLE NOT NULL COMMENT '잔고',
    version BIGINT NOT NULL DEFAULT 0 COMMENT 'optimistic lock',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간'
);

-- wallet_transaction 테이블 생성
CREATE TABLE wallet.wallet_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '거래 ID',
    member_id BIGINT NOT NULL COMMENT '회원 ID',
    type VARCHAR(50) NOT NULL COMMENT '거래 유형 (입금/출금/구매 등)',
    amount DOUBLE NOT NULL COMMENT '거래 금액',
    purchase_id BIGINT COMMENT '상품 구매 내역 ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간'
);

CREATE INDEX idx_wallet_transaction_member_id_created_at ON wallet.wallet_transaction (member_id, created_at);
CREATE INDEX idx_wallet_transaction_type ON wallet.wallet_transaction (type);

-- product 테이블 생성
CREATE TABLE wallet.product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상품 ID',
    name VARCHAR(255) NOT NULL COMMENT '상품 이름',
    price DOUBLE NOT NULL COMMENT '상품 가격',
    product_type VARCHAR(50) NOT NULL COMMENT '상품 유형 (일반상품/쿠폰 등)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간'
);

CREATE INDEX idx_product_product_type ON wallet.product (product_type);

-- purchase 테이블 생성
CREATE TABLE wallet.purchase (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '구매 ID',
    member_id BIGINT NOT NULL COMMENT '회원 ID',
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    purchase_amount DOUBLE NOT NULL COMMENT '거래 금액',
    purchase_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '구매 시간',
    is_canceled BOOLEAN DEFAULT FALSE COMMENT '취소 여부',
    canceled_at TIMESTAMP COMMENT '취소 시간'
);

CREATE INDEX idx_purchase_member_id_is_canceled ON wallet.purchase (member_id,is_canceled);
CREATE INDEX idx_purchase_purchase_at ON wallet.purchase (purchase_at);
CREATE INDEX idx_purchase_canceled_at ON wallet.purchase (canceled_at);
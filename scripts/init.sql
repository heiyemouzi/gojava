-- =============================================================
-- gojava SaaS 初始化脚本（本地 MySQL root/root）
-- 按服务拆库，演示微服务数据隔离；每个服务连自己的库
-- =============================================================

-- ---------- auth-center 库 ----------
CREATE DATABASE IF NOT EXISTS gojava_auth DEFAULT CHARSET utf8mb4;
USE gojava_auth;

CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    password_hash VARCHAR(72)  NOT NULL COMMENT 'bcrypt hash, cost=12',
    tenant_id     VARCHAR(32)  NOT NULL DEFAULT 'default',
    real_name     VARCHAR(64),
    roles         VARCHAR(255) NOT NULL DEFAULT 'user' COMMENT '逗号分隔',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='用户表';

-- admin / Admin@123 (bcrypt cost=12)
INSERT INTO sys_user(username, password_hash, tenant_id, real_name, roles, status)
VALUES ('admin',
        '$2b$12$bkO3JzPIe527DdmFy44X1OqzgmdcZZLj79aLyEX650jlo.mWD8T1.',
        'default', '超级管理员', 'admin,user', 1)
ON DUPLICATE KEY UPDATE username=username;

-- 租户表（SaaS 多租户）
CREATE TABLE IF NOT EXISTS tenant (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id   VARCHAR(32)  NOT NULL UNIQUE,
    name        VARCHAR(128) NOT NULL COMMENT '租户公司名',
    package     VARCHAR(32)  NOT NULL DEFAULT 'free' COMMENT 'free/pro/enterprise',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0停用',
    expired_at  DATE         NULL COMMENT '套餐到期日',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT='租户表';

INSERT INTO tenant(tenant_id, name, package, status, expired_at) VALUES
('default', '默认企业(演示)', 'enterprise', 1, '2027-12-31'),
('acme',    'ACME 科技有限公司', 'pro', 1, '2027-06-30'),
('globex',  'Globex 贸易', 'free', 1, '2026-12-31')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ---------- product-service 库 ----------
CREATE DATABASE IF NOT EXISTS gojava_product DEFAULT CHARSET utf8mb4;
USE gojava_product;

CREATE TABLE IF NOT EXISTS product (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id    VARCHAR(32)  NOT NULL DEFAULT 'default',
    sku          VARCHAR(64)  NOT NULL UNIQUE,
    name         VARCHAR(128) NOT NULL,
    price_cents  BIGINT       NOT NULL COMMENT '单价，单位：分',
    stock        INT          NOT NULL DEFAULT 0 COMMENT '库存数量',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='商品/库存/价格表';

INSERT INTO product(tenant_id, sku, name, price_cents, stock, status) VALUES
('default', 'SKU-APPLE-001',  '红富士苹果 5斤装',    3990, 100, 1),
('default', 'SKU-BANANA-002', '菲律宾香蕉 1把',      1590, 200, 1),
('default', 'SKU-ORANGE-003', '赣南脐橙 10斤装',     5990,  50, 1),
('default', 'SKU-PEAR-004',   '新疆香梨 5斤装',      4990,   0, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ---------- order-service 库 ----------
CREATE DATABASE IF NOT EXISTS gojava_order DEFAULT CHARSET utf8mb4;
USE gojava_order;

CREATE TABLE IF NOT EXISTS `order` (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no       VARCHAR(32)  NOT NULL UNIQUE COMMENT '订单号',
    tenant_id      VARCHAR(32)  NOT NULL,
    user_id        BIGINT       NOT NULL,
    username       VARCHAR(64)  NOT NULL,
    total_cents    BIGINT       NOT NULL COMMENT '订单总额，单位：分',
    status         VARCHAR(16)  NOT NULL DEFAULT 'CREATED'
                   COMMENT 'CREATED已创建/CANCELLED已取消',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) COMMENT='订单主表';

CREATE TABLE IF NOT EXISTS order_item (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id      BIGINT       NOT NULL,
    product_id    BIGINT       NOT NULL,
    sku           VARCHAR(64)  NOT NULL,
    product_name  VARCHAR(128) NOT NULL,
    price_cents   BIGINT       NOT NULL,
    qty           INT          NOT NULL,
    KEY idx_order (order_id)
) COMMENT='订单明细';

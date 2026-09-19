-- 公司资产管理系统：MySQL 8.0 建库、建表和测试数据脚本
CREATE DATABASE IF NOT EXISTS asset_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE asset_management;

CREATE TABLE IF NOT EXISTS sys_department
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    parent_id   BIGINT DEFAULT 0,
    leader      VARCHAR(50),
    phone       VARCHAR(30),
    sort_no     INT    DEFAULT 0,
    status      INT    DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME
);
CREATE TABLE IF NOT EXISTS sys_role
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code   VARCHAR(50) UNIQUE,
    role_name   VARCHAR(100),
    description VARCHAR(255),
    status      INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME
);
CREATE TABLE IF NOT EXISTS sys_user
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(100) NOT NULL,
    real_name     VARCHAR(50),
    phone         VARCHAR(30),
    department_id BIGINT,
    role_id       BIGINT,
    status        INT DEFAULT 1,
    create_time   DATETIME,
    update_time   DATETIME
);
CREATE TABLE IF NOT EXISTS sys_permission
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_code VARCHAR(100),
    permission_name VARCHAR(100),
    resource_path   VARCHAR(255),
    method          VARCHAR(20),
    parent_id       BIGINT DEFAULT 0,
    create_time     DATETIME,
    update_time     DATETIME
);
CREATE TABLE IF NOT EXISTS sys_role_permission
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    create_time   DATETIME,
    update_time   DATETIME,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
);
CREATE TABLE IF NOT EXISTS sys_config
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key   VARCHAR(100) UNIQUE,
    config_value VARCHAR(500),
    description  VARCHAR(255),
    create_time  DATETIME,
    update_time  DATETIME
);
CREATE TABLE IF NOT EXISTS asset
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    asset_code     VARCHAR(50) UNIQUE,
    name           VARCHAR(100),
    category       VARCHAR(50),
    brand          VARCHAR(50),
    model          VARCHAR(100),
    purchase_price DECIMAL(12, 2),
    purchase_date  DATE,
    supplier       VARCHAR(100),
    location       VARCHAR(100),
    status         VARCHAR(30) DEFAULT 'IN_STOCK',
    keeper_id      BIGINT,
    department_id  BIGINT,
    remark         VARCHAR(500),
    create_time    DATETIME,
    update_time    DATETIME
);
CREATE TABLE IF NOT EXISTS asset_operation
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    operation_no  VARCHAR(50),
    type          VARCHAR(30),
    asset_id      BIGINT,
    asset_name    VARCHAR(100),
    quantity      INT DEFAULT 1,
    applicant_id  BIGINT,
    department_id BIGINT,
    handler_id    BIGINT,
    status        VARCHAR(30),
    expected_date DATE,
    reason        TEXT,
    remark        TEXT,
    create_time   DATETIME,
    update_time   DATETIME
);
CREATE TABLE IF NOT EXISTS approval
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    approval_no   VARCHAR(50),
    business_type VARCHAR(30),
    business_id   BIGINT,
    title         VARCHAR(255),
    applicant_id  BIGINT,
    approver_id   BIGINT,
    status        VARCHAR(30),
    current_step  INT,
    comment       TEXT,
    create_time   DATETIME,
    update_time   DATETIME
);
CREATE TABLE IF NOT EXISTS sys_notification
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT,
    title       VARCHAR(100),
    content     TEXT,
    type        VARCHAR(30),
    read_flag   INT DEFAULT 0,
    create_time DATETIME,
    update_time DATETIME
);

-- 密码均为 123456（首次启动后可通过 /api/auth/register 新建测试用户）
INSERT IGNORE INTO sys_department
VALUES (1, '总经办', 0, '张总', '13800000001', 1, 1, NOW(), NOW()),
       (2, '技术部', 0, '李经理', '13800000002', 2, 1, NOW(), NOW()),
       (3, '行政部', 0, '王经理', '13800000003', 3, 1, NOW(), NOW());
INSERT IGNORE INTO sys_role
VALUES (1, 'ADMIN', '系统管理员', '系统全部权限', 1, NOW(), NOW()),
       (2, 'ASSET_ADMIN', '资产管理员', '资产台账与库存管理', 1, NOW(), NOW()),
       (3, 'EMPLOYEE', '普通员工', '申请、领用和归还资产', 1, NOW(), NOW());
INSERT IGNORE INTO sys_user
VALUES (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', '13800000001', 1, 1,
        1, NOW(), NOW()),
       (2, 'zhangsan', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张三', '13800000011', 2, 3, 1,
        NOW(), NOW());
INSERT IGNORE INTO sys_permission
VALUES (1, 'asset:list', '资产列表', '/api/assets', 'GET', 0, NOW(), NOW()),
       (2, 'asset:operate', '资产流程操作', '/api/assets/operations', 'POST', 0, NOW(), NOW()),
       (3, 'approval:decide', '审批处理', '/api/approvals/{id}/approve', 'POST', 0, NOW(), NOW());
INSERT IGNORE INTO sys_role_permission
VALUES (1, 1, 1, NOW(), NOW()),
       (2, 1, 2, NOW(), NOW()),
       (3, 1, 3, NOW(), NOW()),
       (4, 2, 1, NOW(), NOW()),
       (5, 2, 2, NOW(), NOW());
INSERT IGNORE INTO sys_config
VALUES (1, 'company.name', '示例科技有限公司', '公司名称', NOW(), NOW()),
       (2, 'asset.lowStockThreshold', '5', '低库存提醒阈值', NOW(), NOW());
INSERT IGNORE INTO asset
VALUES (1, 'AST-20260001', 'MacBook Pro 14', '电脑', 'Apple', 'M3 Pro', 15999.00, '2026-01-10', '苹果授权经销商',
        '技术部库房', 'IN_USE', 2, 2, '开发设备', NOW(), NOW()),
       (2, 'AST-20260002', '戴尔显示器', '显示器', 'Dell', 'U2723QE', 3999.00, '2026-02-15', '戴尔', '行政库房',
        'IN_STOCK', NULL, 3, '4K 显示器', NOW(), NOW()),
       (3, 'AST-20260003', '办公椅', '办公家具', '赫曼米勒', 'Aeron', 9800.00, '2025-06-20', '办公家具供应商', '技术部',
        'IN_STOCK', NULL, 2, '人体工学椅', NOW(), NOW());
INSERT IGNORE INTO asset_operation
VALUES (1, 'OP20260001', 'ASSET_RECEIVE', 1, 'MacBook Pro 14', 1, 2, 2, 1, 'APPROVED', '2026-01-12',
        '新员工开发设备领用', '已发放', NOW(), NOW());
INSERT IGNORE INTO approval
VALUES (1, 'AP20260001', 'ASSET_RECEIVE', 1, 'ASSET_RECEIVE：MacBook Pro 14', 2, 1, 'APPROVED', 1, '同意领用', NOW(),
        NOW());
INSERT IGNORE INTO sys_notification
VALUES (1, 2, '领用审批通过', '您的 MacBook Pro 14 领用申请已通过。', 'APPROVAL', 0, NOW(), NOW());

-- ============================================================
-- 图书管理系统 数据库初始化脚本
-- 技术栈: MySQL 8 + InnoDB + utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `book_manager` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `book_manager`;

-- ============================================================
-- 1. 系统用户表 sys_user
-- ============================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name`   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `role`        TINYINT      NOT NULL DEFAULT 2 COMMENT '角色: 1=管理员, 2=普通读者',
    `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删, 1=已删',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_username` (`username`) USING BTREE,
    INDEX `idx_role` (`role`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ============================================================
-- 2. 图书分类表 book_category
-- ============================================================
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序号(越小越靠前)',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删, 1=已删',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_category_name` (`category_name`) USING BTREE,
    INDEX `idx_sort` (`sort`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书分类表';

-- ============================================================
-- 3. 图书信息表 book
-- ============================================================
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `book_name`   VARCHAR(200) NOT NULL COMMENT '书名',
    `author`      VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `publisher`   VARCHAR(200) DEFAULT NULL COMMENT '出版社',
    `isbn`        VARCHAR(20)  DEFAULT NULL COMMENT 'ISBN编号',
    `category_id` BIGINT       NOT NULL COMMENT '分类ID',
    `stock`       INT          NOT NULL DEFAULT 0 COMMENT '当前库存',
    `total_stock` INT          NOT NULL DEFAULT 0 COMMENT '总库存',
    `description` TEXT         DEFAULT NULL COMMENT '图书简介',
    `cover`       VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1=上架, 0=下架',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删, 1=已删',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_isbn` (`isbn`) USING BTREE,
    INDEX `idx_category_id` (`category_id`) USING BTREE,
    INDEX `idx_book_name` (`book_name`) USING BTREE,
    INDEX `idx_author` (`author`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE,
    INDEX `idx_cs` (`category_id`, `status`) USING BTREE COMMENT '联合索引-分类+状态'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书信息表';

-- ============================================================
-- 4. 借阅记录表 borrow_record
-- ============================================================
DROP TABLE IF EXISTS `borrow_record`;
CREATE TABLE `borrow_record` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `book_id`     BIGINT       NOT NULL COMMENT '图书ID',
    `borrow_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借书时间',
    `return_time` DATETIME     DEFAULT NULL COMMENT '还书时间',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0=借出中, 1=已归还',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删, 1=已删',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_book_id` (`book_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_borrow_time` (`borrow_time`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted`) USING BTREE,
    INDEX `idx_ubs` (`user_id`, `book_id`, `status`) USING BTREE COMMENT '联合索引-用户+图书+状态',
    CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_borrow_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- ============================================================
-- 5. 初始化数据
-- ============================================================

-- 管理员账号: admin / 123456 (BCrypt加密)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`)
VALUES ('admin', '$2a$10$ismyLPoZkDaOac3s2gZnk.RfSQQ5Z.bfWT2Hkw7Vfg2cBGTaSJaVu', '系统管理员', 'admin@book.com', '13800000000', 1, 1);

-- 普通读者账号: reader / 123456
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`)
VALUES ('reader', '$2a$10$ismyLPoZkDaOac3s2gZnk.RfSQQ5Z.bfWT2Hkw7Vfg2cBGTaSJaVu', '普通读者', 'reader@book.com', '13800000001', 2, 1);

-- 初始化图书分类
INSERT INTO `book_category` (`id`, `category_name`, `sort`, `status`) VALUES
(1, '计算机科学', 1, 1),
(2, '文学小说', 2, 1),
(3, '经济管理', 3, 1),
(4, '自然科学', 4, 1),
(5, '历史哲学', 5, 1);

-- 初始化图书
INSERT INTO `book` (`id`, `book_name`, `author`, `publisher`, `isbn`, `category_id`, `stock`, `total_stock`, `description`) VALUES
(1, '深入理解Java虚拟机', '周志明', '机械工业出版社', '9787111421900', 1, 10, 10, 'Java虚拟机经典入门书籍'),
(2, 'Spring实战（第6版）', 'Craig Walls', '人民邮电出版社', '9787115546070', 1, 8, 8, 'Spring框架权威指南'),
(3, '三体', '刘慈欣', '重庆出版社', '9787536692930', 2, 15, 15, '中国科幻文学里程碑'),
(4, '百年孤独', '加西亚·马尔克斯', '南海出版公司', '9787544253994', 2, 12, 12, '魔幻现实主义经典'),
(5, '国富论', '亚当·斯密', '商务印书馆', '9787100007005', 3, 5, 5, '经济学奠基之作'),
(6, '时间简史', '史蒂芬·霍金', '湖南科学技术出版社', '9787535732304', 4, 6, 6, '探索宇宙的奥秘'),
(7, '人类简史', '尤瓦尔·赫拉利', '中信出版社', '9787508647357', 5, 9, 9, '从动物到上帝');

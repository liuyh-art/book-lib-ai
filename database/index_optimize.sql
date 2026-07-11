-- ============================================================
-- 图书管理系统 - 索引优化脚本
-- 使用: mysql -u root -p book_manager < index_optimize.sql
-- ============================================================

USE book_manager;

-- ============================================================
-- 1. sys_user 表
-- 问题: idx_is_deleted 单独索引无意义（0/1 二值，选择性差）
--       selectPage 中 role/status 查询无合适复合索引
-- ============================================================
-- 删除无用索引
DROP INDEX `idx_is_deleted` ON `sys_user`;
DROP INDEX `idx_status` ON `sys_user`;

-- 添加复合索引（覆盖 role + is_deleted 条件）
ALTER TABLE `sys_user` ADD INDEX `idx_role_deleted` (`role`, `is_deleted`);
ALTER TABLE `sys_user` ADD INDEX `idx_status_deleted` (`status`, `is_deleted`);
ALTER TABLE `sys_user` ADD INDEX `idx_username_deleted` (`username`, `is_deleted`);


-- ============================================================
-- 2. book_category 表
-- 问题: ORDER BY sort ASC, id DESC 无匹配索引
--       idx_is_deleted 单独无用
--       countBooksByCategory 无索引
-- ============================================================
DROP INDEX `idx_is_deleted` ON `book_category`;
DROP INDEX `idx_sort` ON `book_category`;

-- 覆盖排序查询: ORDER BY sort ASC, id DESC
ALTER TABLE `book_category` ADD INDEX `idx_sort_id` (`sort`, `id`);
-- 覆盖分类+启用状态的查询
ALTER TABLE `book_category` ADD INDEX `idx_status_sort` (`status`, `sort`, `id`);


-- ============================================================
-- 3. book 表
-- 问题: idx_is_deleted 单独无用
--       idx_book_name(book_name) 可改为复合
--       idx_author(author) 可改为复合
--       selectAll(status=1) 无好索引
-- ============================================================
DROP INDEX `idx_is_deleted` ON `book`;
DROP INDEX `idx_book_name` ON `book`;
DROP INDEX `idx_author` ON `book`;

-- LIKE 查询索引（实际效果有限，重在配合 is_deleted 过滤）
ALTER TABLE `book` ADD INDEX `idx_name_deleted` (`book_name`, `is_deleted`);
ALTER TABLE `book` ADD INDEX `idx_author_deleted` (`author`, `is_deleted`);

-- 覆盖 selectAll: WHERE status=1 AND is_deleted=0 ORDER BY id DESC
ALTER TABLE `book` ADD INDEX `idx_list_all` (`status`, `is_deleted`, `id` DESC);

-- idx_cs(category_id, status) 已是好索引，保留
-- 再扩展一个带 is_deleted 的覆盖更全的查询
ALTER TABLE `book` ADD INDEX `idx_cat_stat_del` (`category_id`, `status`, `is_deleted`);


-- ============================================================
-- 4. borrow_record 表
-- 问题: idx_is_deleted 单独无用
--       idx_user_id 与 idx_ubs 前缀重复
--       idx_status 与 idx_ubs 前缀重叠
--       countReturnBetween 查 return_time，无索引
--       countBorrowBetween 查 borrow_time，idx_borrow_time 可加 is_deleted
-- ============================================================
DROP INDEX `idx_is_deleted` ON `borrow_record`;
DROP INDEX `idx_user_id` ON `borrow_record`;
DROP INDEX `idx_status` ON `borrow_record`;
DROP INDEX `idx_ubs` ON `borrow_record`;

-- 核心联合索引: 覆盖 selectBorrowing / countBorrowingByUser
ALTER TABLE `borrow_record` ADD INDEX `idx_user_book_status` (`user_id`, `book_id`, `status`, `is_deleted`);

-- 时间范围查询索引
ALTER TABLE `borrow_record` ADD INDEX `idx_borrow_time_del` (`borrow_time`, `is_deleted`);
ALTER TABLE `borrow_record` ADD INDEX `idx_return_time_del` (`return_time`, `is_deleted`);

-- countTotalBorrowed: WHERE status=0 AND is_deleted=0
ALTER TABLE `borrow_record` ADD INDEX `idx_status_del` (`status`, `is_deleted`);

-- 外键索引（InnoDB 自动会给外键加索引，但显式声明更清晰）
ALTER TABLE `borrow_record` ADD INDEX `idx_book_id_del` (`book_id`, `is_deleted`);


-- ============================================================
-- 验证: 查看最终索引
-- ============================================================
SELECT '=== sys_user ===' AS '';
SHOW INDEX FROM `sys_user`;

SELECT '=== book_category ===' AS '';
SHOW INDEX FROM `book_category`;

SELECT '=== book ===' AS '';
SHOW INDEX FROM `book`;

SELECT '=== borrow_record ===' AS '';
SHOW INDEX FROM `borrow_record`;

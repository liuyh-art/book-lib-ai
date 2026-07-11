-- ============================================================
-- 修复用户密码哈希（正确的 BCrypt 密码: 123456）
-- 如果已执行过旧版 init.sql，运行此脚本修复
-- ============================================================

USE book_manager;

UPDATE `sys_user`
SET `password` = '$2a$10$ismyLPoZkDaOac3s2gZnk.RfSQQ5Z.bfWT2Hkw7Vfg2cBGTaSJaVu'
WHERE `username` IN ('admin', 'reader');

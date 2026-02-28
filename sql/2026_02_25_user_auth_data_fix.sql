-- 用户名/手机号重复数据检查与修复示例
-- 执行前请先备份，并在测试环境验证。

-- 1) 查找用户名重复
SELECT username, COUNT(*) AS cnt
FROM account
WHERE username IS NOT NULL AND username <> ''
GROUP BY username
HAVING COUNT(*) > 1;

-- 2) 查找手机号重复
SELECT phone, COUNT(*) AS cnt
FROM account
WHERE phone IS NOT NULL AND phone <> ''
GROUP BY phone
HAVING COUNT(*) > 1;

-- 3) 修复策略示例（手动选择保留的主记录）
-- 方案：对重复记录追加后缀，确保唯一
-- 示例：将重复用户名追加 _dup + id 后 4 位
-- UPDATE account
-- SET username = CONCAT(username, '_dup_', RIGHT(id, 4))
-- WHERE id IN (要修复的ID列表);

-- 4) 修复后再次检查是否仍存在重复
-- SELECT username, COUNT(*) AS cnt FROM account GROUP BY username HAVING COUNT(*) > 1;
-- SELECT phone, COUNT(*) AS cnt FROM account GROUP BY phone HAVING COUNT(*) > 1;

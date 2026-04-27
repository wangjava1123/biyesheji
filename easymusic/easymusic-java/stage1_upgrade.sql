-- 第一阶段升级脚本
-- 目标：
-- 1. 补齐作品发布状态字段
-- 2. 回填历史作品的发布状态与发布时间
-- 3. 根据点赞行为明细回填 good_count

ALTER TABLE `music_info`
    ADD COLUMN IF NOT EXISTS `publish_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发布状态 0:草稿 1:已发布 2:已隐藏' AFTER `music_type`,
    ADD COLUMN IF NOT EXISTS `publish_time` datetime DEFAULT NULL COMMENT '发布时间' AFTER `publish_status`;

UPDATE `music_info`
SET `publish_status` = 1,
    `publish_time` = IFNULL(`publish_time`, `create_time`)
WHERE `music_status` = 1
  AND (`publish_status` IS NULL OR `publish_status` = 0);

UPDATE `music_info` m
LEFT JOIN (
    SELECT `music_id`, COUNT(1) AS cnt
    FROM `music_info_action`
    GROUP BY `music_id`
) t ON m.`music_id` = t.`music_id`
SET m.`good_count` = IFNULL(t.cnt, 0);

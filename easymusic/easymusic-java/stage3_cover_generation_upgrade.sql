-- 第三阶段升级脚本
-- 目标：
-- 1. 新增 AI 封面生成记录表
-- 2. 为 music_info 增加封面来源和生成次数字段
-- 3. 增加 AI 封面价格字典配置

CREATE TABLE IF NOT EXISTS `music_cover_creation` (
    `cover_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '封面生成记录ID',
    `music_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音乐ID',
    `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
    `creation_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创作ID',
    `prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '封面提示词',
    `style` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '视觉风格',
    `model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片模型',
    `integral` int DEFAULT '0' COMMENT '积分消耗',
    `task_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '第三方任务ID',
    `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成后的封面地址',
    `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0:生成中 1:成功 2:失败',
    `fail_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '失败原因',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`cover_id`) USING BTREE,
    KEY `idx_music_id` (`music_id`) USING BTREE,
    KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='AI封面生成记录';

ALTER TABLE `music_info`
    ADD COLUMN IF NOT EXISTS `cover_source` tinyint(1) NOT NULL DEFAULT '0' COMMENT '封面来源 0:手动上传 1:AI生成' AFTER `publish_time`,
    ADD COLUMN IF NOT EXISTS `cover_generate_count` int NOT NULL DEFAULT '0' COMMENT 'AI封面生成次数' AFTER `cover_source`;

INSERT INTO `sys_dict` (`dict_id`, `dict_code`, `dict_pcode`, `dict_value`, `dict_name`, `sort`)
SELECT 101, 'cover_price', '0', '', 'AI封面价格', 0
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `dict_id` = 101 OR (`dict_code` = 'cover_price' AND `dict_pcode` = '0')
);

INSERT INTO `sys_dict` (`dict_id`, `dict_code`, `dict_pcode`, `dict_value`, `dict_name`, `sort`)
SELECT 102, 'default', 'cover_price', '10', 'AI封面生成(10积分/次)', 1
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict` WHERE `dict_id` = 102 OR (`dict_code` = 'default' AND `dict_pcode` = 'cover_price')
);

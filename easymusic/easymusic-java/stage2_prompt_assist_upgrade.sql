-- 第二阶段升级脚本
-- 目标：
-- 1. 新增提示词增强记录表
-- 2. 为 music_creation 增加提示词来源追踪字段

CREATE TABLE IF NOT EXISTS `prompt_optimize_record` (
    `record_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '记录ID',
    `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
    `biz_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务类型',
    `raw_prompt` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '原始提示词',
    `optimized_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '优化后的音乐提示词',
    `structured_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '结构化结果JSON',
    `music_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联作品ID',
    `provider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '供应商标识',
    `model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '模型标识',
    `integral` int DEFAULT '0' COMMENT '积分消耗',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 1:成功 2:失败',
    `fail_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '失败原因',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`record_id`) USING BTREE,
    KEY `idx_user_id` (`user_id`) USING BTREE,
    KEY `idx_music_id` (`music_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='提示词增强记录';

ALTER TABLE `music_creation`
    ADD COLUMN IF NOT EXISTS `origin_prompt` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '原始提示词' AFTER `prompt`,
    ADD COLUMN IF NOT EXISTS `prompt_source_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '提示词来源 0:手填 1:AI增强' AFTER `origin_prompt`,
    ADD COLUMN IF NOT EXISTS `prompt_record_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '提示词增强记录ID' AFTER `prompt_source_type`;

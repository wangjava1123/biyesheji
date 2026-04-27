-- MySQL dump 10.13  Distrib 8.0.35, for Win64 (x86_64)
--
-- Host: localhost    Database: ll-music
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `ll-music`
--

/*!40000 DROP DATABASE IF EXISTS `ll-music`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ll-music` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `ll-music`;

--
-- Table structure for table `music_creation`
--

DROP TABLE IF EXISTS `music_creation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `music_creation` (
  `creation_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创作ID',
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `prompt` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提示词',
  `lyrics` varchar(1500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '歌词',
  `model` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模型',
  `music_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '音乐类型 0:音乐 1:纯音乐',
  `mode_type` tinyint(1) DEFAULT NULL COMMENT '模式 0:简单模式 1:专家模式',
  `settings` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设置信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`creation_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='音乐创作信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music_creation`
--

LOCK TABLES `music_creation` WRITE;
/*!40000 ALTER TABLE `music_creation` DISABLE KEYS */;
INSERT INTO `music_creation` VALUES ('g9CNRbBcSerZ3lC','983332031840','写一首甜蜜浪漫的情人节情歌',NULL,'V3',0,0,'{\"musicEmotion\":null,\"musicGener\":null,\"musicSex\":null}','2026-03-06 20:50:53');
/*!40000 ALTER TABLE `music_creation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `music_info`
--

DROP TABLE IF EXISTS `music_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `music_info` (
  `music_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音乐ID',
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `task_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务ID',
  `creation_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '创作ID',
  `music_title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
  `cover` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面',
  `audio_path` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '音乐地址',
  `duration` int DEFAULT NULL COMMENT '持续时间',
  `lyrics` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '歌词',
  `play_count` int DEFAULT '0' COMMENT '播放数量',
  `good_count` int DEFAULT '0' COMMENT '点赞数',
  `commend_type` tinyint(1) DEFAULT '0' COMMENT '0:未推荐 1:已推荐',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `music_status` tinyint(1) DEFAULT '0' COMMENT '0:生成音乐中 1:生成完毕',
  `music_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '音乐类型 0:音乐 1:纯音乐',
  PRIMARY KEY (`music_id`) USING BTREE,
  UNIQUE KEY `idx_key_task_id` (`task_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='音乐信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music_info`
--

LOCK TABLES `music_info` WRITE;
/*!40000 ALTER TABLE `music_info` DISABLE KEYS */;
INSERT INTO `music_info` VALUES ('801856385427','983332031840','l2m_SPnXW1SRvee5ZaTiG','g9CNRbBcSerZ3lC','情人节温柔','https://wangjinmusic.oss-cn-beijing.aliyuncs.com/file/202603/801856385427.png?t=1772801768408','https://wangjinmusic.oss-cn-beijing.aliyuncs.com/file/202603/GAUd3wfQaRKBTgC3hc8q0MTnFJh2Jp.mp3',98,'[{\"end\":4.501,\"start\":4.481,\"text\":\"[intro]\"},{\"end\":5.841,\"start\":5.821,\"text\":\"[verse]\"},{\"end\":15.7,\"start\":13.103,\"text\":\"晨光洒满你的发梢\"},{\"end\":18.924,\"start\":16.343,\"text\":\"咖啡香气暖了微笑\"},{\"end\":22.22,\"start\":19.564,\"text\":\"走过街头牵你的手\"},{\"end\":25.35,\"start\":22.645,\"text\":\"爱在心底悄悄发芽\"},{\"end\":25.645,\"start\":25.625,\"text\":\"[chorus]\"},{\"end\":28.83,\"start\":25.985,\"text\":\"玫瑰绽放在二月街角\"},{\"end\":31.97,\"start\":29.206,\"text\":\"每一眼都是甜蜜预告\"},{\"end\":35.21,\"start\":32.387,\"text\":\"你是写进星光的诗行\"},{\"end\":40.75,\"start\":35.547,\"text\":\"情人节只想和你环绕\"},{\"end\":41.468,\"start\":41.448,\"text\":\"[inst]\"},{\"end\":47.77,\"start\":47.75,\"text\":\"[chorus]\"},{\"end\":54.32,\"start\":51.551,\"text\":\"风吹过你的红脸庞\"},{\"end\":57.55,\"start\":54.731,\"text\":\"世界安静如初雪收藏\"},{\"end\":62.9,\"start\":57.952,\"text\":\"轻声许下不变的愿望\"},{\"end\":71.105,\"start\":65.153,\"text\":\"陪你走到梦的远方\"},{\"end\":71.175,\"start\":71.155,\"text\":\"[chorus]\"},{\"end\":76.81,\"start\":71.195,\"text\":\"树影描出我们的形状\"},{\"end\":80.0,\"start\":77.176,\"text\":\"甜甜温柔溢满过往\"},{\"end\":83.2,\"start\":80.376,\"text\":\"相拥是最美的信仰\"},{\"end\":89.5,\"start\":83.557,\"text\":\"时光见证浪漫流淌\"},{\"end\":98.0,\"start\":95.079,\"text\":\"[inst]\"}]',2,0,0,'2026-03-06 20:50:53',1,0);
/*!40000 ALTER TABLE `music_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `music_info_action`
--

DROP TABLE IF EXISTS `music_info_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `music_info_action` (
  `action_id` int NOT NULL AUTO_INCREMENT COMMENT '操作ID',
  `music_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '音乐ID',
  `music_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '音乐用户ID',
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `action_type` tinyint(1) DEFAULT NULL COMMENT '操作类型1:点赞',
  PRIMARY KEY (`action_id`) USING BTREE,
  UNIQUE KEY `idx_key_user_music_id` (`music_id`,`user_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='音乐操作';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music_info_action`
--

LOCK TABLES `music_info_action` WRITE;
/*!40000 ALTER TABLE `music_info_action` DISABLE KEYS */;
/*!40000 ALTER TABLE `music_info_action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pay_code_info`
--

DROP TABLE IF EXISTS `pay_code_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_code_info` (
  `pay_code` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付码',
  `amount` decimal(15,2) DEFAULT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `use_user_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '使用用户ID',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 0:待使用 1:已使用',
  PRIMARY KEY (`pay_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='支付码';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_code_info`
--

LOCK TABLES `pay_code_info` WRITE;
/*!40000 ALTER TABLE `pay_code_info` DISABLE KEYS */;
INSERT INTO `pay_code_info` VALUES ('00101269',1.20,'2025-09-07 10:26:25','100000000000','2025-09-07 10:26:42',1),('60852337',1.20,'2025-09-07 10:44:49',NULL,NULL,0),('72321313',1.00,'2025-08-30 22:40:40','874391377680','2025-08-30 22:04:47',1),('75579432',1.00,'2025-08-30 22:05:38','874391377680','2025-08-30 22:05:53',1),('96533543',1.20,'2025-09-07 10:03:54','100000000000','2025-09-07 10:04:02',1);
/*!40000 ALTER TABLE `pay_code_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pay_order_info`
--

DROP TABLE IF EXISTS `pay_order_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pay_order_info` (
  `order_id` varchar(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付了行',
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户ID',
  `product_id` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品ID',
  `product_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品名称',
  `amount` decimal(5,2) DEFAULT NULL COMMENT '金额',
  `channel_order_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支付通道订单ID',
  `create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '0:待支付 1:支付完成',
  `integral` int DEFAULT NULL COMMENT '购买积分',
  `pay_info` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支付信息',
  `pay_type` tinyint(1) DEFAULT NULL COMMENT '支付类型',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='支付订单信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_order_info`
--

LOCK TABLES `pay_order_info` WRITE;
/*!40000 ALTER TABLE `pay_order_info` DISABLE KEYS */;
INSERT INTO `pay_order_info` VALUES ('20250831144659WBJBW1XQV4E7QB','100000000000','15631','体验卡',1.20,NULL,'2025-08-31 06:47:01',NULL,-1,120,'weixin://wxpay/bizpayurl?pr=wssC6hpz1',1),('20250831151410PUOSEC3RIFZK9P','100000000000','15631','体验卡',1.20,'4200002780202508311308070808','2025-08-31 07:14:12','2025-08-31 07:14:27',1,120,'weixin://wxpay/bizpayurl?pr=L8pufVlz1',1),('20250831202711A90PSC3B0HBPTE','243091801061','15631','体验卡',1.20,NULL,'2025-08-31 20:27:12',NULL,-1,120,'weixin://wxpay/bizpayurl?pr=I7n341lz3',1),('20250831202728HAX945NLTLQGI9','243091801061','15631','体验卡',1.20,'4200002733202508315975893088','2025-08-31 20:27:29','2025-08-31 20:27:38',1,120,'weixin://wxpay/bizpayurl?pr=xHZYfq8z3',1),('20250901114004AXRSLXNZJB8EFU','100000000000','15631','体验卡',1.20,NULL,'2025-09-01 11:40:06',NULL,-1,120,'weixin://wxpay/bizpayurl?pr=TL3bmAuz1',1),('20250901200045MKGPFAGQFU6KKK','363912285032','15631','体验卡',1.20,NULL,'2025-09-01 20:00:46',NULL,-1,120,'weixin://wxpay/bizpayurl?pr=IykEsMUz3',1),('20250907100213OASMAXRFXFHW4R','100000000000','15631','体验卡',1.20,'4200002803202509074906590842','2025-09-07 10:02:14','2025-09-07 10:02:25',1,120,'weixin://wxpay/bizpayurl?pr=vTysfjEz3',1),('20250907100402JDYBZFGH841FXO','100000000000','15631','体验卡',1.20,NULL,'2025-09-07 10:04:02',NULL,1,120,NULL,0),('20250907102544IZ3VYWVMSPMO1B','100000000000','15631','体验卡',1.20,'4200002872202509070839941281','2025-09-07 10:25:45','2025-09-07 10:25:55',1,120,'weixin://wxpay/bizpayurl?pr=R9VT5vCz3',1),('20250907102641THSO4XJYRPR79P','100000000000','15631','体验卡',1.20,NULL,'2025-09-07 10:26:42',NULL,1,120,NULL,0);
/*!40000 ALTER TABLE `pay_order_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_info`
--

DROP TABLE IF EXISTS `product_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_info` (
  `product_id` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品ID',
  `product_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品名称',
  `cover` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '封面',
  `price` decimal(5,2) DEFAULT NULL COMMENT '价格',
  `product_description` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `onsale_type` tinyint(1) DEFAULT NULL COMMENT '上架类型',
  `integral` int DEFAULT NULL COMMENT '购买积分',
  `sort` int DEFAULT NULL COMMENT '排序号',
  PRIMARY KEY (`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='商品信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_info`
--

LOCK TABLES `product_info` WRITE;
/*!40000 ALTER TABLE `product_info` DISABLE KEYS */;
INSERT INTO `product_info` VALUES ('15631','体验卡','202508/1756621727996.png',1.20,'体验卡，用于体验音乐创作，可以使用多种AI模型，生成音乐和纯音乐。','2025-08-23 02:41:07',1,120,1),('45675','畅玩卡','202508/1756621817320.png',20.00,'适合有音乐情怀喜欢自制音乐的朋友，可以使用多种AI模型，生成音乐和纯音乐。','2025-08-23 02:23:25',1,2000,2);
/*!40000 ALTER TABLE `product_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict`
--

DROP TABLE IF EXISTS `sys_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict` (
  `dict_id` int NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典编号',
  `dict_pcode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '父级字典ID',
  `dict_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典值',
  `dict_desc` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典描述',
  `sort` tinyint(1) DEFAULT NULL COMMENT '排序号',
  PRIMARY KEY (`dict_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统字典';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict`
--

LOCK TABLES `sys_dict` WRITE;
/*!40000 ALTER TABLE `sys_dict` DISABLE KEYS */;
INSERT INTO `sys_dict` VALUES (17,'music_model','0','','音乐模型',NULL),(18,'V3','music_model','30','生成最长120s的音乐(30积分/首)',1),(19,'V3.5','music_model','40','生成最长270s的音乐(40积分/首)',2),(20,'music_model_pure','0',NULL,'纯音乐模型',NULL),(21,'V3','music_model_pure','25','生成最长120s的纯音乐(25积分/首)',1),(22,'V3.5','music_model_pure','35','生成最长270s的纯音乐(35积分/首)',2),(23,'music_grenre','0',NULL,'曲风',0),(24,'流行','music_grenre','','',1),(25,'摇滚','music_grenre','','',2),(26,'迪斯科','music_grenre','',NULL,3),(27,'电子','music_grenre','',NULL,4),(28,'民谣','music_grenre','',NULL,5),(29,'放克','music_grenre','',NULL,6),(30,'乡村','music_grenre','',NULL,7),(31,'爵士','music_grenre','',NULL,8),(32,'嘻哈','music_grenre','',NULL,9),(33,'金属','music_grenre','',NULL,10),(34,'蓝调','music_grenre','',NULL,11),(35,'朋克','music_grenre','',NULL,12),(36,'music_emotion','0',NULL,'情绪',0),(37,'放松','music_emotion','','',1),(38,'生气','music_emotion','','',2),(39,'快乐','music_emotion','','',3),(40,'悲伤','music_emotion','','',4),(41,'冷静','music_emotion','','',5),(42,'灵感','music_emotion','','',6),(43,'神秘','music_emotion','','',7),(44,'雄伟','music_emotion','','',8),(45,'古怪','music_emotion','','',9),(46,'充满活力','music_emotion','','',10),(47,'music_sex','0',NULL,'人声',0),(48,'女声','music_sex','','',1),(49,'男声','music_sex','','',2),(50,'music_prompt','0',NULL,'音乐提示词',0),(51,'一首梦幻的浪漫歌曲，讲述霓虹灯下时间仿佛静止，每一刻都令人难','music_prompt',NULL,NULL,1),(52,'一首充满力量的主题曲，强烈的嗓音和鼓舞人心的节拍，讲述一起征','music_prompt','','',2),(53,'一首充满活力的浪漫歌曲，分享一个魔幻之吻','music_prompt',NULL,NULL,3),(54,'一首反思性歌曲，带有令人难忘的旋律和强有力的重复副歌','music_prompt',NULL,NULL,4),(55,'一首描述一只猫头鹰在月光下跳舞的歌曲','music_prompt',NULL,NULL,5),(56,'创作一首适合雨天聆听的民谣','music_prompt',NULL,NULL,6),(57,'为雨后的清新小镇创作宁静之歌','music_prompt',NULL,NULL,7),(58,'写一首甜蜜浪漫的情人节情歌','music_prompt',NULL,NULL,8),(61,'一首充满力量的主题曲，强烈的嗓音和鼓舞人心的节拍，讲述一起征','music_prompt',NULL,NULL,11),(62,'music_prompt_pure','0','','纯音乐提示词',0),(63,'轻快的原声吉他搭配轻打击乐，非常适合旅行剪辑和日常生活。','music_prompt_pure',NULL,NULL,1),(64,'欢快的尤克里里旋律配以轻柔的钢琴，适合生活方式和烹饪视频博客','music_prompt_pure',NULL,NULL,2),(65,'带有微妙键盘的低保真嘻哈节拍，非常适合学习环节和桌面导览。','music_prompt_pure',NULL,NULL,3),(66,'带有合成器音波的充满活力的电子流行音乐，科技评测和开箱视频。','music_prompt_pure',NULL,NULL,4),(67,'带有木琴和轻鼓的趣味器乐，非常适合家庭和儿童友好的内容。','music_prompt_pure',NULL,NULL,5),(68,'带有柔和音垫的环境电子音乐，适合自然漫步和宁静时刻。','music_prompt_pure',NULL,NULL,6),(69,'带有渐进弦乐的鼓舞人心的钢琴，励志内容。','music_prompt_pure',NULL,NULL,7),(70,'奇特的复古合成器搭配有趣的贝斯，非常适合喜剧视频博客和挑战。','music_prompt_pure',NULL,NULL,8),(71,'带有丝滑萨克斯风的轻松爵士休息室音乐，适合城市观光和咖啡店场','music_prompt_pure',NULL,NULL,9),(72,'带有现代节拍的动感打击乐，健身和体育视频博客。','music_prompt_pure',NULL,NULL,10),(73,'极简环境嗡鸣声配以偶尔的风铃声，非常适合深度思考和解决问题。','music_prompt_pure',NULL,NULL,11),(74,'平静的自然声音配以远处的钢琴，非常适合放松和减压。','music_prompt_pure',NULL,NULL,12),(75,'柔和的合成器声波配以轻柔的打击乐，完美适合深夜工作环节。','music_prompt_pure',NULL,NULL,13),(76,'极简钢琴配以柔和的氛围纹理，完美适合深度工作和专注。','music_prompt_pure',NULL,NULL,14),(77,'写一首温柔如初的民谣情歌','music_prompt',NULL,NULL,0),(78,'写一首关于舞蹈节奏的活力之歌，使用电子风格','music_prompt',NULL,NULL,0),(79,'写一首关于冬日初雪的宁静之歌，使用民谣风格','music_prompt',NULL,NULL,0),(80,'写一首春日樱花绽放的浪漫之歌，使用流行风格','music_prompt',NULL,NULL,0),(81,'为一次热气球之旅打造梦幻升空的旋律','music_prompt',NULL,NULL,0),(82,'夏日夜晚，微风与星空相伴','music_prompt',NULL,NULL,0),(83,'春日早晨，鸟鸣与花香交织','music_prompt',NULL,NULL,0),(84,'深夜书房，灯火与书籍相伴','music_prompt',NULL,NULL,0),(85,'写一首送给离别恋人的歌，使用民谣风格','music_prompt',NULL,NULL,0),(86,'创作一首流行乐，感受都市的宁静','music_prompt',NULL,NULL,0),(87,'写一首关于星际旅行的歌，使用电子风格','music_prompt',NULL,NULL,0),(88,'创作一首充满力量的说唱，传递正能量与不屈精神','music_prompt',NULL,NULL,0),(89,'想要一首适合早晨起床时听的清新音乐','music_prompt_pure',NULL,NULL,0),(90,'适合晚上酒吧听的爵士蓝调','music_prompt_pure',NULL,NULL,0),(91,'适合海边看日落时听的宁静音乐','music_prompt_pure',NULL,NULL,0),(92,'来一首适合独自旅行时听的自由音乐','music_prompt_pure',NULL,NULL,0),(93,'想要一首适合冬日暖阳下的温馨音乐','music_prompt_pure',NULL,NULL,0),(94,'来一首适合夜晚驾车时听的动感音乐','music_prompt_pure',NULL,NULL,0),(95,'来一首适合团建时激发团队精神的快节奏音乐','music_prompt_pure',NULL,NULL,0),(96,'来一首适合工作间隙放松的轻松音乐','music_prompt_pure',NULL,NULL,0),(97,'一首适合骑行时听的歌','music_prompt_pure',NULL,NULL,0),(98,'来一首适合秋天落叶时听的感伤音乐','music_prompt_pure',NULL,NULL,0),(99,'适合夜晚读书时听的古典音乐','music_prompt_pure',NULL,NULL,0),(100,'想要一首适合家庭聚会时听的欢快音乐','music_prompt_pure',NULL,NULL,0);
/*!40000 ALTER TABLE `sys_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `integral` int DEFAULT '0' COMMENT '积分',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `idx_key_email` (`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES ('100000000000','test@qq.com','程序员老罗','avatar/100000000000.png&1756622795386','47ec2dd791e31e2ef2076caf64ed9b3d',1,'2025-08-31 06:44:57','2025-09-07 09:45:33',480),('243091801061','test02@qq.com','测试账号','avatar/243091801061.png','47ec2dd791e31e2ef2076caf64ed9b3d',1,'2025-08-31 20:26:44','2025-08-31 20:26:58',90),('983332031840','2936221375@qq.com','wangjin','https://wangjinmusic.oss-cn-beijing.aliyuncs.com/file/avatar/983332031840.png','725402dd696e4981d748be2f42c051bb',1,'2026-03-06 20:50:26','2026-03-06 20:50:33',9970);
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_integral_record`
--

DROP TABLE IF EXISTS `user_integral_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_integral_record` (
  `record_id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户ID',
  `change_integral` int DEFAULT NULL COMMENT '积分',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `record_type` tinyint DEFAULT NULL COMMENT '记录类型 0:创作失败退回 1:创作消耗 2:充值 3:系统赠送',
  `business_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务ID',
  `amount` decimal(5,2) DEFAULT NULL COMMENT '充值金额',
  PRIMARY KEY (`record_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10043 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户积分记录信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_integral_record`
--

LOCK TABLES `user_integral_record` WRITE;
/*!40000 ALTER TABLE `user_integral_record` DISABLE KEYS */;
INSERT INTO `user_integral_record` VALUES (10031,'100000000000',120,'2025-08-31 07:14:27',2,'20250831151410PUOSEC3RIFZK9P',1.20),(10032,'100000000000',-30,'2025-08-31 07:14:57',1,'WPXQMEDIj44DYAR',NULL),(10033,'243091801061',120,'2025-08-31 20:27:38',2,'20250831202728HAX945NLTLQGI9',1.20),(10034,'243091801061',-30,'2025-08-31 20:28:06',1,'SZHiTTrXUsrFKME',NULL),(10035,'100000000000',-30,'2025-09-07 09:49:27',1,'r0aUpgKzrXZgn1Q',NULL),(10036,'100000000000',-30,'2025-09-07 09:58:28',1,'7So8DVIUdsDWttT',NULL),(10037,'100000000000',120,'2025-09-07 10:02:25',2,'20250907100213OASMAXRFXFHW4R',1.20),(10038,'100000000000',120,'2025-09-07 10:04:02',2,'20250907100402JDYBZFGH841FXO',1.20),(10039,'100000000000',-30,'2025-09-07 10:23:09',1,'R8d7LlMbbq6ryAv',NULL),(10040,'100000000000',120,'2025-09-07 10:25:55',2,'20250907102544IZ3VYWVMSPMO1B',1.20),(10041,'100000000000',120,'2025-09-07 10:26:42',2,'20250907102641THSO4XJYRPR79P',1.20),(10042,'983332031840',-30,'2026-03-06 20:50:53',1,'g9CNRbBcSerZ3lC',NULL);
/*!40000 ALTER TABLE `user_integral_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-06 23:19:03

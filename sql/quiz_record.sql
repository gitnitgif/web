/*
 Navicat Premium Data Transfer

 Source Server         : cc
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3306
 Source Schema         : fjdialect

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 14/04/2026 00:30:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for quiz_record
-- ----------------------------
DROP TABLE IF EXISTS `quiz_record`;
CREATE TABLE `quiz_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户 ID',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `total_questions` int(0) NOT NULL COMMENT '总题数',
  `correct_count` int(0) NOT NULL COMMENT '正确数',
  `score` int(0) NOT NULL COMMENT '得分',
  `city_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '测试城市',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户测验记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

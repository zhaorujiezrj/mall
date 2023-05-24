/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : mall-test

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 24/05/2023 08:46:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oauth2_authorization
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization`;
CREATE TABLE `oauth2_authorization`  (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `registered_client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `principal_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `authorization_grant_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `authorized_scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `attributes` blob NULL,
  `state` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `authorization_code_value` blob NULL,
  `authorization_code_issued_at` timestamp NULL DEFAULT NULL,
  `authorization_code_expires_at` timestamp NULL DEFAULT NULL,
  `authorization_code_metadata` blob NULL,
  `access_token_value` blob NULL,
  `access_token_issued_at` timestamp NULL DEFAULT NULL,
  `access_token_expires_at` timestamp NULL DEFAULT NULL,
  `access_token_metadata` blob NULL,
  `access_token_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `access_token_scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `oidc_id_token_value` blob NULL,
  `oidc_id_token_issued_at` timestamp NULL DEFAULT NULL,
  `oidc_id_token_expires_at` timestamp NULL DEFAULT NULL,
  `oidc_id_token_metadata` blob NULL,
  `refresh_token_value` blob NULL,
  `refresh_token_issued_at` timestamp NULL DEFAULT NULL,
  `refresh_token_expires_at` timestamp NULL DEFAULT NULL,
  `refresh_token_metadata` blob NULL,
  `user_code_value` blob NULL,
  `user_code_issued_at` timestamp NULL DEFAULT NULL,
  `user_code_expires_at` timestamp NULL DEFAULT NULL,
  `user_code_metadata` blob NULL,
  `device_code_value` blob NULL,
  `device_code_issued_at` timestamp NULL DEFAULT NULL,
  `device_code_expires_at` timestamp NULL DEFAULT NULL,
  `device_code_metadata` blob NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oauth2_authorization
-- ----------------------------

-- ----------------------------
-- Table structure for oauth2_authorization_consent
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_authorization_consent`;
CREATE TABLE `oauth2_authorization_consent`  (
  `registered_client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `principal_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `authorities` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`registered_client_id`, `principal_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oauth2_authorization_consent
-- ----------------------------
INSERT INTO `oauth2_authorization_consent` VALUES ('8a40068d-23d0-4cfd-b1f3-94d46c99bd19', 'admin', 'SCOPE_message.read');
INSERT INTO `oauth2_authorization_consent` VALUES ('8a40068d-23d0-4cfd-b1f3-94d46c99bd19', 'user', 'SCOPE_message.read');

-- ----------------------------
-- Table structure for oauth2_registered_client
-- ----------------------------
DROP TABLE IF EXISTS `oauth2_registered_client`;
CREATE TABLE `oauth2_registered_client`  (
  `id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `client_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `client_id_issued_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `client_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `client_secret_expires_at` timestamp NULL DEFAULT NULL,
  `client_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `client_authentication_methods` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `authorization_grant_types` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `post_logout_redirect_uris` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `scopes` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `client_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `token_settings` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oauth2_registered_client
-- ----------------------------
INSERT INTO `oauth2_registered_client` VALUES ('8a40068d-23d0-4cfd-b1f3-94d46c99bd19', 'client', '2023-05-19 13:26:46', '{bcrypt}$2a$10$ePye4Ft9Ci6N3mWk84BPsue/cRzu1epiVyQqksB4x1kbRjMzObHSu', NULL, '8a40068d-23d0-4cfd-b1f3-94d46c99bd19', 'client_secret_basic', 'refresh_token,client_credentials,authorization_code,password,sms_code', 'https://baidu.com', '', 'openid,profile,message.read,message.write', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.client.require-proof-key\":false,\"settings.client.require-authorization-consent\":true}', '{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"settings.token.reuse-refresh-tokens\":false,\"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],\"settings.token.access-token-time-to-live\":[\"java.time.Duration\",7200.000000000],\"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},\"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",10800.000000000],\"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",3600.000000000],\"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父节点id',
  `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '父节点id路径',
  `sort` int NULL DEFAULT 0 COMMENT '显示顺序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态(1:正常;0:禁用)',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标识(1:已删除;0:未删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 160 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, '有来技术', 0, '0', 1, 1, 0, NULL, NULL);
INSERT INTO `sys_dept` VALUES (2, '研发部门', 1, '0,1', 1, 1, 0, NULL, '2022-04-19 12:46:37');
INSERT INTO `sys_dept` VALUES (3, '测试部门', 1, '0,1', 1, 1, 0, NULL, '2022-04-19 12:46:37');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型编码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项名称',
  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典项值',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态(1:正常;0:禁用)',
  `defaulted` tinyint NULL DEFAULT 0 COMMENT '是否默认(1:是;0:否)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES (1, 'gender', '男', '1', 1, 1, 0, NULL, '2019-05-05 13:07:52', '2022-06-12 23:20:39');
INSERT INTO `sys_dict_item` VALUES (2, 'gender', '女', '2', 2, 1, 0, NULL, '2019-04-19 11:33:00', '2019-07-02 14:23:05');
INSERT INTO `sys_dict_item` VALUES (3, 'gender', '未知', '0', 1, 1, 0, NULL, '2020-10-17 08:09:31', '2020-10-17 08:09:31');
INSERT INTO `sys_dict_item` VALUES (6, 'grant_type', '密码模式', 'password', 1, 1, 0, NULL, '2020-10-17 09:11:52', '2021-01-31 09:48:18');
INSERT INTO `sys_dict_item` VALUES (7, 'grant_type', '授权码模式', 'authorization_code', 1, 1, 0, NULL, '2020-10-17 09:12:15', '2020-12-14 10:11:00');
INSERT INTO `sys_dict_item` VALUES (8, 'grant_type', '客户端模式', 'client_credentials', 1, 1, 0, NULL, '2020-10-17 09:12:36', '2020-12-14 10:11:00');
INSERT INTO `sys_dict_item` VALUES (9, 'grant_type', '刷新模式', 'refresh_token', 1, 1, 0, NULL, '2020-10-17 09:12:57', '2021-01-08 17:33:12');
INSERT INTO `sys_dict_item` VALUES (10, 'grant_type', '简化模式', 'implicit', 1, 1, 0, NULL, '2020-10-17 09:13:23', '2020-12-14 10:11:00');
INSERT INTO `sys_dict_item` VALUES (11, 'micro_service', '系统服务', 'youlai-admin', 1, 1, 0, NULL, '2021-06-17 00:14:12', '2021-06-17 00:14:12');
INSERT INTO `sys_dict_item` VALUES (12, 'micro_service', '会员服务', 'youlai-ums', 2, 1, 0, NULL, '2021-06-17 00:15:06', '2021-06-17 00:15:06');
INSERT INTO `sys_dict_item` VALUES (13, 'micro_service', '商品服务', 'youlai-pms', 3, 1, 0, NULL, '2021-06-17 00:15:26', '2021-06-17 00:16:18');
INSERT INTO `sys_dict_item` VALUES (14, 'micro_service', '订单服务', 'youlai-oms', 4, 1, 0, NULL, '2021-06-17 00:15:40', '2021-06-17 00:16:10');
INSERT INTO `sys_dict_item` VALUES (15, 'micro_service', '营销服务', 'youlai-sms', 5, 1, 0, NULL, '2021-06-17 00:16:01', '2021-06-17 00:16:01');
INSERT INTO `sys_dict_item` VALUES (16, 'request_method', '不限', '*', 1, 1, 0, NULL, '2021-06-17 00:18:34', '2021-06-17 00:18:34');
INSERT INTO `sys_dict_item` VALUES (17, 'request_method', 'GET', 'GET', 2, 1, 0, NULL, '2021-06-17 00:18:55', '2021-06-17 00:18:55');
INSERT INTO `sys_dict_item` VALUES (18, 'request_method', 'POST', 'POST', 3, 1, 0, NULL, '2021-06-17 00:19:06', '2021-06-17 00:19:06');
INSERT INTO `sys_dict_item` VALUES (19, 'request_method', 'PUT', 'PUT', 4, 1, 0, NULL, '2021-06-17 00:19:17', '2021-06-17 00:19:17');
INSERT INTO `sys_dict_item` VALUES (20, 'request_method', 'DELETE', 'DELETE', 5, 1, 0, NULL, '2021-06-17 00:19:30', '2021-06-17 00:19:30');
INSERT INTO `sys_dict_item` VALUES (21, 'request_method', 'PATCH', 'PATCH', 6, 1, 0, NULL, '2021-06-17 00:19:42', '2021-06-17 00:19:42');
INSERT INTO `sys_dict_item` VALUES (22, 'grant_type', '验证码', 'captcha', 1, 1, 0, '', '2022-05-28 11:44:28', '2022-05-28 11:44:28');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '类型编码',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态(0:正常;1:禁用)',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `type_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '性别', 'gender', 1, NULL, '2019-12-06 19:03:32', '2022-06-12 16:21:28');
INSERT INTO `sys_dict_type` VALUES (2, '授权方式', 'grant_type', 1, NULL, '2020-10-17 08:09:50', '2021-01-31 09:48:24');
INSERT INTO `sys_dict_type` VALUES (3, '微服务列表', 'micro_service', 1, NULL, '2021-06-17 00:13:43', '2021-06-17 00:17:22');
INSERT INTO `sys_dict_type` VALUES (4, '请求方式', 'request_method', 1, NULL, '2021-06-17 00:18:07', '2021-06-17 00:18:07');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父菜单ID',
  `type` tinyint NULL DEFAULT NULL COMMENT '菜单类型(1：菜单；2：目录；3：外链；4：按钮)',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单名称',
  `path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由路径(浏览器地址栏路径)',
  `component` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径(vue页面完整路径，省略.vue后缀)',
  `perm` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按钮权限标识',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单图标',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `visible` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:禁用;1:开启)',
  `redirect` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '跳转路径',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, 2, '系统管理', '/system', 'Layout', NULL, 'system', 1, 1, '/system/user', '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (2, 1, 1, '用户管理', 'user', 'system/user/index', NULL, 'user', 1, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (3, 1, 1, '角色管理', 'role', 'system/role/index', NULL, 'role', 2, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (4, 1, 1, '菜单管理', 'cmenu', 'system/menu/index', NULL, 'menu', 3, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (5, 1, 1, '部门管理', 'dept', 'system/dept/index', NULL, 'tree', 4, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (6, 1, 1, '字典管理', 'dict', 'system/dict/index', NULL, 'dict', 5, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (7, 1, 1, '客户端管理', 'client', 'system/client/index', NULL, 'client', 6, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (9, 0, 2, '营销管理', '/sms', 'Layout', NULL, 'number', 5, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (10, 9, 1, '广告列表', 'advert', 'sms/advert/index', NULL, 'advert', 1, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (11, 0, 1, '商品管理', '/pms', 'Layout', NULL, 'goods', 2, 1, '/pms/goods', '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (12, 11, 1, '商品列表', 'goods', 'pms/goods/index', NULL, 'goods-list', 1, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (13, 0, 1, '订单管理', '/oms', 'Layout', NULL, 'shopping', 3, 1, '/oms/order', '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (14, 13, 1, '订单列表', 'order', 'oms/order/index', NULL, 'order', 1, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (15, 0, 1, '会员管理', '/ums', 'Layout', NULL, 'user', 4, 1, '/ums/member', '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (16, 15, 1, '会员列表', 'member', 'ums/member/index', NULL, 'peoples', 1, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (17, 11, 1, '品牌管理', 'brand', 'pms/brand/index', NULL, 'brand', 5, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (18, 11, 1, '商品分类', 'category', 'pms/category/index', NULL, 'menu', 3, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (19, 11, 1, '商品上架', 'goods-detail', 'pms/goods/detail', NULL, 'publish', 2, 1, NULL, '2021-08-28 09:12:21', '2021-08-28 09:12:21');
INSERT INTO `sys_menu` VALUES (20, 0, 2, '多级菜单', '/multi-level-menu', 'Layout', NULL, 'nested', 7, 1, '/nested/level1/level2', '2022-02-16 23:11:00', '2022-02-16 23:11:00');
INSERT INTO `sys_menu` VALUES (21, 20, 1, '菜单一级', 'nested_level1_index', 'nested/level1/index', NULL, '', 1, 1, '/nested/level1/level2', '2022-02-16 23:13:38', '2022-02-16 23:13:38');
INSERT INTO `sys_menu` VALUES (22, 21, 1, '菜单二级', 'nested_level1_level2_index', 'nested/level1/level2/index', NULL, '', 1, 1, '/nested/level1/level2/level3', '2022-02-16 23:14:23', '2022-02-16 23:14:23');
INSERT INTO `sys_menu` VALUES (23, 22, 1, '菜单三级-1', 'nested_level1_level2_level3_index1', 'nested/level1/level2/level3/index1', NULL, '', 1, 1, '', '2022-02-16 23:14:51', '2022-02-16 23:14:51');
INSERT INTO `sys_menu` VALUES (24, 22, 1, '菜单三级-2', 'nested_level1_level2_level3_index2', 'nested/level1/level2/level3/index2', NULL, '', 2, 1, '', '2022-02-16 23:15:08', '2022-02-16 23:15:08');
INSERT INTO `sys_menu` VALUES (26, 0, 1, '外部链接', '/external-link', 'Layout', NULL, 'link', 9, 1, 'noredirect', '2022-02-17 22:51:20', '2022-02-17 22:51:20');
INSERT INTO `sys_menu` VALUES (30, 26, 3, 'document', 'https://www.cnblogs.com/haoxianrui/', '', NULL, 'link', 1, 1, '', '2022-02-18 00:01:40', '2022-02-18 00:01:40');
INSERT INTO `sys_menu` VALUES (32, 0, 2, '实验室', '/lab', 'Layout', NULL, 'lab', 8, 1, 'noredirect', '2022-04-19 09:35:38', '2022-04-19 09:35:38');
INSERT INTO `sys_menu` VALUES (33, 32, 1, 'Seata', 'seata', 'lab/seata/index', NULL, 'security', 1, 1, '', '2022-04-19 09:35:38', '2022-04-19 09:35:38');
INSERT INTO `sys_menu` VALUES (34, 32, 1, 'RabbitMQ', 'rabbitmq', 'lab/rabbit/index', NULL, 'rabbitmq', 2, 1, '', '2022-04-19 09:38:25', '2022-04-19 09:38:25');
INSERT INTO `sys_menu` VALUES (37, 9, 1, '优惠券列表', 'coupon', 'sms/coupon/index', NULL, 'input', 2, 1, '', '2022-05-29 00:24:07', '2022-05-29 00:24:07');
INSERT INTO `sys_menu` VALUES (39, 32, 1, 'Sentinel', 'sentinel', 'lab/sentinel/index', NULL, 'security', 2, 1, '', '2022-07-23 09:52:41', '2022-07-23 09:52:41');
INSERT INTO `sys_menu` VALUES (40, 2, 4, '新增用户', '', NULL, 'sys:user:add', '', 1, 1, '', NULL, NULL);
INSERT INTO `sys_menu` VALUES (41, 2, 4, '修改用户', '', NULL, 'sys:user:edit', '', 2, 1, '', '2022-11-05 01:26:44', '2022-11-05 01:26:44');
INSERT INTO `sys_menu` VALUES (42, 2, 4, '删除用户', '', NULL, 'sys:user:del', '', 3, 1, '', '2022-11-05 01:27:13', '2022-11-05 01:27:13');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '角色名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色编码',
  `sort` int NULL DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint NULL DEFAULT 1 COMMENT '角色状态(1-正常；0-停用)',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除标识(0-未删除；1-已删除)',
  `data_scope` tinyint NULL DEFAULT NULL COMMENT '数据权限(0-所有数据；1-部门及子部门数据；2-本部门数据；3-本人数据)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'ROOT', 1, 1, 0, 0, '2021-05-21 14:56:51', '2018-12-23 16:00:00');
INSERT INTO `sys_role` VALUES (2, '系统管理员', 'ADMIN', 1, 1, 0, 0, '2021-03-25 12:39:54', '2022-11-05 00:22:02');
INSERT INTO `sys_role` VALUES (3, '访问游客', 'GUEST', 3, 1, 0, 30, '2021-05-26 15:49:05', '2019-05-05 16:00:00');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 40);
INSERT INTO `sys_role_menu` VALUES (2, 41);
INSERT INTO `sys_role_menu` VALUES (2, 42);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 5);
INSERT INTO `sys_role_menu` VALUES (2, 6);
INSERT INTO `sys_role_menu` VALUES (2, 7);
INSERT INTO `sys_role_menu` VALUES (2, 11);
INSERT INTO `sys_role_menu` VALUES (2, 12);
INSERT INTO `sys_role_menu` VALUES (2, 19);
INSERT INTO `sys_role_menu` VALUES (2, 18);
INSERT INTO `sys_role_menu` VALUES (2, 17);
INSERT INTO `sys_role_menu` VALUES (2, 13);
INSERT INTO `sys_role_menu` VALUES (2, 14);
INSERT INTO `sys_role_menu` VALUES (2, 15);
INSERT INTO `sys_role_menu` VALUES (2, 16);
INSERT INTO `sys_role_menu` VALUES (2, 9);
INSERT INTO `sys_role_menu` VALUES (2, 10);
INSERT INTO `sys_role_menu` VALUES (2, 37);
INSERT INTO `sys_role_menu` VALUES (2, 20);
INSERT INTO `sys_role_menu` VALUES (2, 21);
INSERT INTO `sys_role_menu` VALUES (2, 22);
INSERT INTO `sys_role_menu` VALUES (2, 23);
INSERT INTO `sys_role_menu` VALUES (2, 24);
INSERT INTO `sys_role_menu` VALUES (2, 32);
INSERT INTO `sys_role_menu` VALUES (2, 33);
INSERT INTO `sys_role_menu` VALUES (2, 34);
INSERT INTO `sys_role_menu` VALUES (2, 39);
INSERT INTO `sys_role_menu` VALUES (2, 26);
INSERT INTO `sys_role_menu` VALUES (2, 30);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) NULL DEFAULT 1 COMMENT '性别((1:男;2:女))',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `dept_id` int NULL DEFAULT NULL COMMENT '部门ID',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户头像',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '用户状态((1:正常;0:禁用))',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除标识(0:未删除;1:已删除)',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `login_name`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'root', '超级用户', 0, '$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq', NULL, 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621210123', 1, '', 0, '2023-04-12 01:31:29', '2023-04-12 01:31:29', 1, 1);
INSERT INTO `sys_user` VALUES (2, 'admin', '系统管理员', 1, '$2a$10$HYuL7ELt84W25USmHD3wPOyDxbVQaTB70CGA5Gg1oUj5ZfIm6bl1q', 2, 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621210123', 1, '', 0, '2023-04-12 01:31:29', '2023-04-12 01:31:29', 1, 1);
INSERT INTO `sys_user` VALUES (3, 'test', '测试小用户', 1, '$2a$10$MPJkNw.hKT/fZOgwYP8q9eu/rFJJDsNov697AmdkHNJkpjIpVSw2q', 3, 'https://s2.loli.net/2022/04/07/gw1L2Z5sPtS8GIl.gif', '17621210123', 1, '', 0, '2023-04-12 01:31:29', '2023-04-12 01:31:29', 1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);

-- ----------------------------
-- Table structure for ums_member
-- ----------------------------
DROP TABLE IF EXISTS `ums_member`;
CREATE TABLE `ums_member`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `gender` tinyint(1) NULL DEFAULT NULL,
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `openid` char(28) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `session_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `point` int NULL DEFAULT 0 COMMENT '会员积分',
  `deleted` tinyint(1) NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `balance` bigint NULL DEFAULT 1000000000,
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `country` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `language` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 77 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ums_member
-- ----------------------------
INSERT INTO `ums_member` VALUES (1, 1, '郝先瑞', '17621590365', NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/J31cY2qVWviaOqhjPlr18VY5ic1SUvDESG1mQkicQfFugWibYe7VJIhYJBZYDBib0T4TJVhUOtiaW1TGkJRqIWd3K0dQ/132', 'oUBUG5hAB_8EMrSaqd2HjJQBFg74', NULL, 1, 0, 0, '2022-02-26 20:59:20', '2022-02-26 20:59:20', 1000000000, '', '', 'zh_CN', '');
INSERT INTO `ums_member` VALUES (2, 1, 'Flamesky', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eorwiaJcRPxKMNHgov0HGBRA8JODQrhw67x61FGEFwic2E2UlhXSKmQ455jqT5RIPsZjmpkdia0pyZdA/132', 'oEMah4qx8utBwve1_5U2bq4z9Ucw', NULL, 1, 0, 0, '2021-01-12 17:52:03', NULL, 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (3, 1, '非洲小白脸', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEIIs1glKcYOadLFibr2et98eXTADdicLUGrQqF8EtvicIu5e5TwOkuBAzIf8zEl0aYPJaDkfIHTOEWuQ/132', 'oH-MK0V-N4Lotq-kXIMAMjLdXdtY', NULL, 1, 0, 0, '2021-01-12 17:52:06', NULL, 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (4, 1, '花花的世界', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLxWhtkFhVKpfXib0BibMaIzeOAVCGVScnR5ibsibdENiaibjvnfy7AxeSSCTbn9IBvqMe1iaJ6BWTxIjZtg/132', 'oUBUG5lBhnnn-HBCF9mYMZbQv7A8', NULL, 1, 0, 0, '2021-01-12 17:52:09', '2021-02-24 22:43:41', 999680200, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (5, 1, '微尘', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/vNZqQTZRAia4sz17MJeeXeqhzaBbIEzEXGvgwG4l1KQg2mQAb3eB1q9HLnVJUo4u8OSNSv1seuqHxNPKyYicb4Dw/132', 'oZ75o5Kk-opj_ioVOj6vTO7-K0HM', NULL, 1, 0, 0, '2021-01-12 17:52:12', NULL, 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (6, 1, '大田', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIPhsrhOFictxk44ialrd4gZP6SyFmz4v2rHBZ3C72O0KsKQDTHlVBqtoSJ5uiaPAvD0t9F5VBjbruQw/132', 'oUBUG5m_9Gfa3bZmzP_tRNnX2vsg', NULL, 1, 0, 0, '2021-01-12 17:52:16', NULL, 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (7, 1, '看好路，向前走！', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/lZibiaShtph66QznR6yiarR7VsBkkjqGqPCwqDGD8WlaxnllcjG7SRiaX0DOujFXX8epAbyvFpHv03uI83xXFhdwZA/132', 'oUBUG5tj5LF8IjJDU2s1cqSdthdo', NULL, 1, 0, 0, NULL, NULL, 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (8, 1, 'CIAO！', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/0d5kVzsH20SUXzPjbgamFn7DraWURYE7GJX15rMSVVDCeHN3kKW3ZozlUichS7Ch5jXADocWYW3jzBTj24oZVKw/132', 'oEeWo5IXHzfBqOQRI5mzSAfzoN2A', NULL, 1, 0, 0, NULL, NULL, 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (10, 1, '时光会咬人', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJyb4yl7JJCDKNX3yRwGjZ8fdXBTSVaW9cQIErvibmDR08m0vsrqWonxvRibrFxric0wqAKgVFa1IBlg/132', 'oUBUG5mQmw7d5XMwci78J6nDApA0', NULL, 1, 0, 0, '2021-01-30 17:22:18', NULL, 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (12, 1, 'I', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJGjiauanv1bnlJxKiaBQtalptWZUnCnE725cS8SjWoVAjLDuFLg3sKDfumhKuMs7NGHIc0gz8dNopQ/132', 'oUBUG5rX8pS8O__e3Li-owjcCWvg', NULL, 1, 0, 0, '2021-02-01 19:45:57', '2021-02-01 19:45:57', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (14, 1, '77777777', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83ercp0SnvuleWloRkX8y5pibLHtg2OoKGECJH7udBoAoicsO87ibjmsUMiaDgJAJ8ibaiavGv1aEQicle8lMA/132', 'oUBUG5mOf9q54E6ob0kDC_cymoiw', NULL, 1, 0, 0, '2021-02-02 19:49:50', '2021-02-02 19:49:50', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (15, 1, 'Max_Qiu', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIspnkuj3p0Ly4v6dIz5nClVLnNvIE5BVyd6ORaz6kLrwsxbicfqnG7ic4JpqWedpqk1lgx71QlHauQ/132', 'owHt46JBw46D1cCP8kyLefAoB8Ss', NULL, 1, 0, 0, '2021-02-04 12:08:16', '2021-02-04 12:08:16', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (16, 0, '神经蛙', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q3auHgzwzM4Z6nX9KwiaJy2momCR0BLGnXF7ibI9WCJNTpqiaWDYS80624vRibsWr1muV2N8qM5wia0n5lSxOvttjNA/132', 'oUBUG5i0B3nUFejoqSlNwFlcJ_oc', NULL, 1, 0, 0, '2021-02-16 15:48:07', '2021-02-16 15:48:07', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (17, 1, '自渡.', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/jAiavgRK2sHGs29TfZOlfGibBEpkq5btJQcVib2OoOibDTrsbC3d1R2LEtyEN48Cx8pQBZE174k13ribJamUkrD1ctg/132', 'oUBUG5l9OT1HaIZmOKJciVVaiZaA', NULL, 1, 0, 0, '2021-02-17 13:11:54', '2021-02-17 13:11:54', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (18, 1, 'lxm', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKCMRO8bKSzryP9QD8DqOHyaVP8ibK41qpviaqNpTN3IDibiapjqLibibIZS7LrQTfiaNV6YNhYn8vzqcviaw/132', 'oEJcR0faCvzl8xF5fMKPoPBZQwB8', NULL, 1, 0, 0, '2021-02-20 16:47:34', '2021-02-20 16:47:34', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (19, 1, '林育挺', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/pHghIy3YR0f1pyWuENRiaqic03azQnbW6YtjyWrfl0bXZjF4J9UA5QPG9jXUe8BymtngqJ0zPwnS0VSPLIBBJEiaw/132', 'oUBUG5oCupLtC1SaawIk9uotF66U', NULL, 1, 0, 0, '2021-02-20 22:12:13', '2021-02-20 22:12:13', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (20, 1, '香蕉皮i', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Z1BicLpfe2ygKc91pm1LhKdLKUtFPdyn4lSkVkA5Pn5iaI5lT3h4M4dFAanxGKEMfPIgOCZjxjiaIHLuqq9Fn5E0Q/132', 'oUBUG5soM0AUAgP8PM8e0M2X5qxs', NULL, 1, 0, 0, '2021-02-24 12:09:26', '2021-02-24 12:09:26', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (22, 1, 'F', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/geyibXYyaoODjLy2aYP54WjUxYz71pwOMDrnRWBXibgh2gDr4hZuw5qiawic75oacEXYxRicykCRINube7MFd9ANicrw/132', 'oUBUG5glvFzDxwkFEK7MUsRcBqhI', NULL, 1, 0, 0, '2021-03-22 21:37:51', '2021-03-22 21:37:51', 999060500, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (23, 1, '蓝动', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/9cfWYQjJMKsplTQQLJqR3A75j9Hib44jHF0vIEJqfHC2ttfg0GCiaSzQbSQVVxrgicAJallo3eB2qsGyE1Z4RNYCQ/132', 'oUBUG5nOlD91HYvXRhCqKrMSWzUs', NULL, 1, 0, 0, '2021-03-23 17:35:03', '2021-03-23 17:35:03', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (24, 1, '路亚小生', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/tnib4ZCXWGOznmtyoHBL5BFicYZWICNyic0EyPWk70kr9IWzHSCVdIqFKN2o7BxyuYaDib0ogmfpuMTBgo3pOibPt9A/132', 'oUBUG5l8zS6fstLbQh4GNf81w438', NULL, 1, 0, 0, '2021-03-24 12:06:23', '2021-03-24 12:06:23', 1000000000, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (25, 1, 'Alan', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/QohQ9hnZnxF2mJOM1RywBPqToNVicDpeF8KdXrwmtYnRyoWaBHk0R25T1wxzleCJV3Un8iappa70yn8fJmgGAZnQ/132', 'oUBUG5kQ2YvWcX7OIYpw8owuWGqE', NULL, 1, 0, 0, '2021-03-29 15:57:24', '2021-03-29 15:57:24', 999380300, NULL, NULL, NULL, NULL);
INSERT INTO `ums_member` VALUES (40, 1, '秋城', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/ajNVdqHZLLDfyM5iaYFwhzQ1Xv9zyA3bXDV42niazQlibiajdXba0YK4yAFFWIMY7vwfI1ny8Ej8pm0pmp7OkC2afg/132', 'oUBUG5oPQnarJi7g2mXE_svcHVeU', NULL, 1, 0, 0, '2021-06-19 16:57:51', '2021-06-19 16:57:51', 1000000000, '张家口', '中国', 'zh_CN', '河北');
INSERT INTO `ums_member` VALUES (72, 0, 'Benji', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/WjvlGaEv9ajwHfpEGWd4KwjloXjCgvibMibPQiaL7I4ausEzp0rH0AHqKpJeERS2UHiaLSlftOYCj77nLEVNtXZqcQ/132', 'oBdKY4iltAU9HBHUBC-S8p3H1vv4', NULL, 1, 0, 0, '2022-03-13 04:23:33', '2022-03-13 04:23:33', 1000000000, '', '', 'zh_CN', '');
INSERT INTO `ums_member` VALUES (73, 0, '_六月流星', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/gZlE19RpbIuqmicAdL8wC7u26gx6LhRwsicc3icFGYA8TNvy6RJnyGUMTbWkyhg1lJ4yolnOVqCs6gI7Oiaby8lqSQ/132', 'oUBUG5rsrAjW173G25vgL2hq6AZk', NULL, 1, 0, 0, '2022-03-14 16:17:33', '2022-03-14 16:17:33', 1000000000, '', '', 'zh_CN', '');
INSERT INTO `ums_member` VALUES (74, 0, 'zhang', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIOca9icPByKxn3Z6AZAL3l0xfmOOqbRQVn1f2qoqtOYw3bJliawrTvu4F9Tg2aAHicASicXrW74zVUYA/132', 'oYCwr5IaYCS7web81wifWivPEMGw', NULL, 1, 0, 0, '2022-03-16 11:18:36', '2022-03-16 11:18:36', 1000000000, '', '', 'zh_CN', '');
INSERT INTO `ums_member` VALUES (75, 0, '德才-Edward', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/4DgXefgtM24MXSMPomsvuwCiav9v77bA5Ou8S74qrlAALfNPMEqD938jrNVybP5sJzfaKyqHrYcicbIwU8Xcc1YA/132', 'oRJIA43TMiAw76HRFEMli4BSvX6c', NULL, 1, 0, 0, '2022-04-06 18:14:07', '2022-04-06 18:14:07', 1000000000, '', '', 'zh_CN', '');
INSERT INTO `ums_member` VALUES (76, 0, '小乐有点笨', NULL, NULL, 'https://thirdwx.qlogo.cn/mmopen/vi_32/gTdG3Zs0OMqruJpGAKu9mZxo4K9WjEzmG759HiaBS5I71LVxktv2zyqNcKiaxeNF3JTSn1epfU3gUz8hZWdqhfPg/132', 'oTAHp5OexvW68nRkn5ISV0zKvEGE', NULL, 1, 0, 0, '2022-04-22 18:55:39', '2022-04-22 18:55:39', 1000000000, '', '', 'zh_CN', '');

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `hk_change_log`;
CREATE TABLE `hk_change_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '业务key',
  `biz_type` int(11) NOT NULL COMMENT '业务类型：1规则变更；2worker变更',
  `from_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '原始值',
  `to_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '目标值',
  `app_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '数据所属APP',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `uuid` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '防重ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uuid`(`uuid`) USING BTREE COMMENT '防重索引'
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;



DROP TABLE IF EXISTS `hk_user`;
CREATE TABLE `hk_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `nick_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '昵称',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `pwd` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密码',
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '手机号',
  `role` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '角色：ADMIN-超管，APPADMIN-app管理员，APPUSER-app用户',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `state` int(11) NOT NULL DEFAULT 1 COMMENT '状态：1可用；0冻结',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_userName`(`user_name`) USING BTREE COMMENT '账号唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of hk_user
-- ----------------------------
INSERT INTO `hk_user` VALUES (1, 'admin', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '1234567890123', 'ADMIN', '', '2020-04-24 02:28:33', 1);



DROP TABLE IF EXISTS `hk_key_rule`;
CREATE TABLE `hk_key_rule`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'key',
  `prefix` int(11) NOT NULL COMMENT '是否前缀：1是；0否',
  `intervals` int(11) NOT NULL COMMENT '间隔时间（秒）',
  `threshold` int(11) NOT NULL COMMENT '阈值',
  `duration` int(11) NOT NULL DEFAULT 60 COMMENT '缓存时间',
  `app_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `state` int(11) NOT NULL COMMENT '状态：1可用；-1删除',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `update_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `version` int(11) NOT NULL DEFAULT 0 COMMENT '数据版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `hk_key_record`;
CREATE TABLE `hk_key_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'key',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `val` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'value',
  `duration` int(11) NOT NULL DEFAULT 60 COMMENT '缓存时间',
  `source` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '来源',
  `type` int(11) NOT NULL DEFAULT 1 COMMENT '记录类型：1put；2del; -1unkonw',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '防重ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`uuid`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `hk_change_log`;
CREATE TABLE `hk_change_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_key` varchar(128)  CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT ''  COMMENT '业务ID',
  `biz_type` int(11) NOT NULL COMMENT '业务类型：1规则变更；2worker变更',
  `from_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '原始值',
  `to_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '目标值',
  `app_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '数据所属APP',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `uuid` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '防重ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`uuid`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;



DROP TABLE IF EXISTS `hk_key_timely`;
CREATE TABLE `hk_key_timely`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'key',
  `val` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'value',
  `uuid` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '防重ID',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `duration` int(11) NOT NULL DEFAULT 0 COMMENT '缓存时间',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`uuid`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of hk_key_timely
-- ----------------------------
INSERT INTO `hk_key_timely` VALUES (1, 'testk1', 'testv1', '/jd/hotkeys/test/', 'test', 60000, 1212121);


DROP TABLE IF EXISTS `hk_app_info`;
CREATE TABLE `hk_app_info` (
  `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_name`        varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '应用名称',
  `principal_name`  varchar(20) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL DEFAULT '' COMMENT '负责人',
  `principal_phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL DEFAULT '' COMMENT '负责人手机号',
  `app_desc`        varchar(64) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL DEFAULT '' COMMENT '应用描述',
  `create_time`     datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建/接入时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`app_name`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;



CREATE TABLE `hk_statistics`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'keyName',
  `count` int(11) NOT NULL COMMENT '计数',
  `app` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT 'app',
  `days` int(11) NOT NULL COMMENT '天数',
  `hours` bigint(11) NOT NULL COMMENT '小时数',
  `minutes` bigint(11) NOT NULL DEFAULT 0 COMMENT '分钟数',
  `biz_type` int(2) NOT NULL COMMENT '业务类型',
  `rule` varchar(180) NOT NULL COMMENT '所属规则',
  `uuid` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '防重ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uuid`(`uuid`) USING BTREE COMMENT '防重唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;


CREATE TABLE `hk_receive_count`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `worker_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'key名称',
  `receive_count` int(11) NOT NULL COMMENT '接收数',
  `hours` int(20) NOT NULL COMMENT '小时数',
  `minutes` bigint(20) NOT NULL COMMENT '分钟数',
  `seconds` bigint(20) NOT NULL COMMENT '秒数',
  `uuid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '防重ID',
  `create_time` datetime(0) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uuid`(`uuid`) USING BTREE COMMENT '防重ID'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;



CREATE TABLE `hk_rules`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rules` varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '规则JSON',
  `app` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '所属APP',
  `update_user` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `version` int(11) NOT NULL DEFAULT 0 COMMENT '版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_app`(`app`) USING BTREE COMMENT '防重索引'
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;



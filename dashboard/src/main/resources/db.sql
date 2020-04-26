
DROP TABLE IF EXISTS `hk_worker`;
CREATE TABLE `hk_worker`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '节点名称',
  `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'ip',
  `port` int(11) NOT NULL COMMENT 'port',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `update_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `state` tinyint(4) NOT NULL COMMENT '状态:1 可用，0不可用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name`) USING BTREE COMMENT '实例名称唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;


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
INSERT INTO `hk_user` VALUES (1, 'lyfa', 'lyf', '202cb962ac59075b964b07152d234b70', '', 'ADMIN', '', '2020-04-24 02:28:33', 1);



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
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`key_name`) USING BTREE COMMENT '唯一索引'
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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of hk_key_record
-- ----------------------------
INSERT INTO `hk_key_record` VALUES (1, '123', '123', '123123124233val', 123, 'lyf', 1, '2020-04-26 09:26:24');
INSERT INTO `hk_key_record` VALUES (2, '123', '123', '123123124233val', 123, 'lyf', 1, '2020-04-26 09:28:13');


DROP TABLE IF EXISTS `hk_change_log`;
CREATE TABLE `hk_change_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` int(11) NOT NULL COMMENT '业务ID',
  `biz_type` int(11) NOT NULL COMMENT '业务类型：1规则变更；2worker变更',
  `from_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '原始值',
  `to_str` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '目标值',
  `app_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '数据所属APP',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `create_time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;



DROP TABLE IF EXISTS `hk_key_timely`;
CREATE TABLE `hk_key_timely`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'key',
  `val` varchar(2048) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'value',
  `parent_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '父级KEY',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `duration` int(11) NOT NULL DEFAULT 0 COMMENT '缓存时间',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Compact;

-- ----------------------------
-- Records of hk_key_timely
-- ----------------------------
INSERT INTO `hk_key_timely` VALUES (1, '/jd/hotkeys/test/testk1', 'testv1', '/jd/hotkeys/test/', 'test', 60000, 1212121);
INSERT INTO `hk_key_timely` VALUES (2, '/jd/hotkeys/test/testk2', 'testv2', '/jd/hotkeys/test/', 'test', 10000, 123443345);
INSERT INTO `hk_key_timely` VALUES (3, '/jd/hotkeys/test/', '', '/jd/hotkeys/', 'test', 2000, 12334343346);
INSERT INTO `hk_key_timely` VALUES (4, '/jd/hotkeys/', '', 'jd/', '', 150000, 12342343543);
INSERT INTO `hk_key_timely` VALUES (5, '/jd/hotkeys/cart/', '', '/jd/hotkeys/', 'cart', 250000, 5767676322);
INSERT INTO `hk_key_timely` VALUES (6, '/jd/hotkeys/cart/testk3', 'testv3', '/jd/hotkeys/cart/', 'cart', 500000, 124546666);
INSERT INTO `hk_key_timely` VALUES (7, '123', '123123124233val', '', '123', 123, 1587893292779);

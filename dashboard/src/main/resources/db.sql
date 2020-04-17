
DROP TABLE IF EXISTS `hk_worker`;
CREATE TABLE `hk_worker`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '节点名称',
  `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'ip',
  `port` int(0) NOT NULL COMMENT 'port',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  `state` tinyint(0) NOT NULL COMMENT '状态:1 可用，0不可用，-1 永久移除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name`) USING BTREE COMMENT '实例名称唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;


CREATE TABLE `hk_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `nick_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '昵称',
  `user_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `pwd` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '密码',
  `phone` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '手机号',
  `role` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '角色：ADMIN-超管，APPADMIN-app管理员，APPUSER-app用户',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `state` int(0) NOT NULL DEFAULT 1 COMMENT '状态：1可用；0冻结; -1永久移除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_userName`(`user_name`) USING BTREE COMMENT '账号唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `hk_key_rule`;
CREATE TABLE `hk_key_rule`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'key',
  `prefix` int(0) NOT NULL COMMENT '是否前缀：1是；0否',
  `interval` int(0) NOT NULL COMMENT '间隔时间（秒）',
  `threshold` int(0) NOT NULL COMMENT '阈值',
  `duration` int(0) NOT NULL DEFAULT 60 COMMENT '缓存时间',
  `app_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `state` int(0) NOT NULL COMMENT '状态：1可用；-1删除',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  `version` int(0) NOT NULL COMMENT '数据版本',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_key`(`key`) USING BTREE COMMENT '唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;



DROP TABLE IF EXISTS `hk_key_record`;
CREATE TABLE `hk_key_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `key` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'key',
  `app_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '所属appName',
  `count` int(0) NOT NULL DEFAULT 1 COMMENT 'key出现的数量',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;



DROP TABLE IF EXISTS `hk_change_log`;
CREATE TABLE `hk_change_log`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_id` int(0) NOT NULL COMMENT '业务ID',
  `biz_type` int(0) NOT NULL COMMENT '业务类型：1规则变更 ',
  `from` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '原始值',
  `to` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '目标值',
  `update_user` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '修改人',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;


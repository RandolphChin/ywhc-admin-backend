-- YWHC后台管理系统数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ywhc_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ywhc_admin`;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标志：0-正常，1-删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(64) NOT NULL COMMENT '角色标识',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    `data_scope` TINYINT DEFAULT 1 COMMENT '数据权限范围：1-全部数据，2-部门数据，3-部门及以下数据，4-仅本人数据',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标志：0-正常，1-删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 3. 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `menu_type` TINYINT NOT NULL COMMENT '菜单类型：0-目录，1-菜单，2-按钮',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `permission` VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
    `icon` VARCHAR(64) DEFAULT NULL COMMENT '图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `is_external` TINYINT DEFAULT 0 COMMENT '是否外链：0-否，1-是',
    `is_cache` TINYINT DEFAULT 1 COMMENT '是否缓存：0-否，1-是',
    `is_visible` TINYINT DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标志：0-正常，1-删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- ----------------------------
-- 4. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 5. 角色菜单权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单权限关联表';

-- ----------------------------
-- 6. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(64) DEFAULT NULL COMMENT '操作用户名',
    `module` VARCHAR(64) DEFAULT NULL COMMENT '操作模块',
    `operation_type` TINYINT DEFAULT NULL COMMENT '操作类型：1-新增，2-修改，3-删除，4-查询，5-登录，6-登出',
    `operation_desc` VARCHAR(255) DEFAULT NULL COMMENT '操作描述',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
    `response_result` TEXT DEFAULT NULL COMMENT '响应结果',
    `execution_time` BIGINT DEFAULT NULL COMMENT '执行时间(毫秒)',
    `status` TINYINT DEFAULT 1 COMMENT '操作状态：0-失败，1-成功',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `location` VARCHAR(255) DEFAULT NULL COMMENT '操作地点',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_module` (`module`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 初始化菜单数据
INSERT INTO `sys_menu` VALUES
(1, 0, '系统管理', 0, '/system', NULL, 'system:manage', 'settings', 1, 0, 1, 1, 1, '系统管理目录', 0, NOW(), NOW(), 1, 1),
(2, 1, '用户管理', 1, '/system/user', 'system/user/index', 'system:user:list', 'people', 1, 0, 1, 1, 1, '用户管理菜单', 0, NOW(), NOW(), 1, 1),
(3, 1, '角色管理', 1, '/system/role', 'system/role/index', 'system:role:list', 'assignment_ind', 2, 0, 1, 1, 1, '角色管理菜单', 0, NOW(), NOW(), 1, 1),
(4, 1, '菜单管理', 1, '/system/menu', 'system/menu/index', 'system:menu:list', 'menu', 3, 0, 1, 1, 1, '菜单管理菜单', 0, NOW(), NOW(), 1, 1),
(5, 1, '日志管理', 1, '/system/log', 'system/log/index', 'system:log:list', 'description', 4, 0, 1, 1, 1, '日志管理菜单', 0, NOW(), NOW(), 1, 1),

(6, 0, '系统监控', 0, '/monitor', NULL, 'monitor:manage', 'monitor', 2, 0, 1, 1, 1, '系统监控目录', 0, NOW(), NOW(), 1, 1),
(7, 6, '在线用户', 1, '/monitor/online', 'monitor/online/index', 'monitor:online:list', 'people_outline', 1, 0, 1, 1, 1, '在线用户菜单', 0, NOW(), NOW(), 1, 1),

-- 用户管理按钮权限
(11, 2, '用户新增', 2, NULL, NULL, 'system:user:add', NULL, 1, 0, 1, 1, 1, '用户新增按钮', 0, NOW(), NOW(), 1, 1),
(12, 2, '用户修改', 2, NULL, NULL, 'system:user:edit', NULL, 2, 0, 1, 1, 1, '用户修改按钮', 0, NOW(), NOW(), 1, 1),
(13, 2, '用户删除', 2, NULL, NULL, 'system:user:delete', NULL, 3, 0, 1, 1, 1, '用户删除按钮', 0, NOW(), NOW(), 1, 1),
(14, 2, '用户导出', 2, NULL, NULL, 'system:user:export', NULL, 4, 0, 1, 1, 1, '用户导出按钮', 0, NOW(), NOW(), 1, 1),
(15, 2, '重置密码', 2, NULL, NULL, 'system:user:resetPwd', NULL, 5, 0, 1, 1, 1, '重置密码按钮', 0, NOW(), NOW(), 1, 1),

-- 角色管理按钮权限
(21, 3, '角色新增', 2, NULL, NULL, 'system:role:add', NULL, 1, 0, 1, 1, 1, '角色新增按钮', 0, NOW(), NOW(), 1, 1),
(22, 3, '角色修改', 2, NULL, NULL, 'system:role:edit', NULL, 2, 0, 1, 1, 1, '角色修改按钮', 0, NOW(), NOW(), 1, 1),
(23, 3, '角色删除', 2, NULL, NULL, 'system:role:delete', NULL, 3, 0, 1, 1, 1, '角色删除按钮', 0, NOW(), NOW(), 1, 1),
(24, 3, '分配权限', 2, NULL, NULL, 'system:role:auth', NULL, 4, 0, 1, 1, 1, '分配权限按钮', 0, NOW(), NOW(), 1, 1),

-- 菜单管理按钮权限
(31, 4, '菜单新增', 2, NULL, NULL, 'system:menu:add', NULL, 1, 0, 1, 1, 1, '菜单新增按钮', 0, NOW(), NOW(), 1, 1),
(32, 4, '菜单修改', 2, NULL, NULL, 'system:menu:edit', NULL, 2, 0, 1, 1, 1, '菜单修改按钮', 0, NOW(), NOW(), 1, 1),
(33, 4, '菜单删除', 2, NULL, NULL, 'system:menu:delete', NULL, 3, 0, 1, 1, 1, '菜单删除按钮', 0, NOW(), NOW(), 1, 1),

-- 在线用户管理按钮权限
(41, 7, '强制下线', 2, NULL, NULL, 'monitor:online:forceLogout', NULL, 1, 0, 1, 1, 1, '强制下线按钮', 0, NOW(), NOW(), 1, 1),
(42, 7, '清理过期', 2, NULL, NULL, 'monitor:online:clean', NULL, 2, 0, 1, 1, 1, '清理过期按钮', 0, NOW(), NOW(), 1, 1);

-- 初始化角色数据
INSERT INTO `sys_role` VALUES
(1, '超级管理员', 'admin', '超级管理员，拥有所有权限', 1, 1, 1, '超级管理员角色', 0, NOW(), NOW(), 1, 1),
(2, '普通用户', 'user', '普通用户，基础权限', 4, 2, 1, '普通用户角色', 0, NOW(), NOW(), 1, 1);

-- 初始化用户数据 (密码: admin123)
INSERT INTO `sys_user` VALUES
(1, 'admin', '$2a$10$7JB720yubVSOfvVWbazBuOWWZHrOqImqwawBM07vYX9/Ls9Fo/.DS', '超级管理员', 'admin@ywhc.com', '13800138000', NULL, 1, 1, NULL, NULL, '系统管理员账号', 0, NOW(), NOW(), 1, 1),
(2, 'user', '$2a$10$7JB720yubVSOfvVWbazBuOWWZHrOqImqwawBM07vYX9/Ls9Fo/.DS', '普通用户', 'user@ywhc.com', '13800138001', NULL, 1, 1, NULL, NULL, '普通用户账号', 0, NOW(), NOW(), 1, 1);

-- 初始化用户角色关联数据
INSERT INTO `sys_user_role` VALUES
(1, 1, 1, NOW(), 1),
(2, 2, 2, NOW(), 1);

-- 初始化角色菜单权限关联数据 (超级管理员拥有所有权限)
INSERT INTO `sys_role_menu` VALUES
(1, 1, 1, NOW(), 1),
(2, 1, 2, NOW(), 1),
(3, 1, 3, NOW(), 1),
(4, 1, 4, NOW(), 1),
(5, 1, 5, NOW(), 1),
(6, 1, 6, NOW(), 1),
(7, 1, 7, NOW(), 1),
(8, 1, 11, NOW(), 1),
(9, 1, 12, NOW(), 1),
(10, 1, 13, NOW(), 1),
(11, 1, 14, NOW(), 1),
(12, 1, 15, NOW(), 1),
(13, 1, 21, NOW(), 1),
(14, 1, 22, NOW(), 1),
(15, 1, 23, NOW(), 1),
(16, 1, 24, NOW(), 1),
(17, 1, 31, NOW(), 1),
(18, 1, 32, NOW(), 1),
(19, 1, 33, NOW(), 1),
(20, 1, 41, NOW(), 1),
(21, 1, 42, NOW(), 1);

-- 普通用户权限 (只有查看权限)
INSERT INTO `sys_role_menu` VALUES
(22, 2, 1, NOW(), 1),
(23, 2, 2, NOW(), 1),
(24, 2, 3, NOW(), 1),
(25, 2, 4, NOW(), 1),
(26, 2, 5, NOW(), 1),
(27, 2, 6, NOW(), 1),
(28, 2, 7, NOW(), 1);

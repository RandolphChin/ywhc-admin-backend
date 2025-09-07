-- 创建部门表
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门ID',
  `dept_name` varchar(30) NOT NULL COMMENT '部门名称',
  `dept_code` varchar(20) NOT NULL COMMENT '部门编码',
  `dept_type` tinyint DEFAULT '2' COMMENT '部门类型：1-公司，2-部门，3-小组',
  `leader_id` bigint DEFAULT NULL COMMENT '负责人ID',
  `leader_name` varchar(20) DEFAULT NULL COMMENT '负责人姓名',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `ancestors` varchar(500) DEFAULT '0' COMMENT '祖级列表',
  `sort_order` int DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint DEFAULT '1' COMMENT '部门状态：0-停用，1-正常',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标志：0-正常，1-删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_code` (`dept_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统部门表';

-- 创建角色部门关联表
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和部门关联表';

-- 修改用户表，添加部门ID字段
ALTER TABLE `sys_user` ADD COLUMN `dept_id` bigint DEFAULT NULL COMMENT '部门ID' AFTER `last_login_ip`;
ALTER TABLE `sys_user` ADD KEY `idx_dept_id` (`dept_id`);

-- 插入初始部门数据
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `dept_type`, `ancestors`, `sort_order`, `status`, `remark`) VALUES
(1, 0, 'YWHC集团', 'YWHC', 1, '0', 0, 1, 'YWHC集团总部'),
(2, 1, '技术部', 'TECH', 2, '0,1', 1, 1, '技术研发部门'),
(3, 1, '市场部', 'MARKET', 2, '0,1', 2, 1, '市场营销部门'),
(4, 1, '人事部', 'HR', 2, '0,1', 3, 1, '人力资源部门'),
(5, 2, '前端组', 'FRONTEND', 3, '0,1,2', 1, 1, '前端开发小组'),
(6, 2, '后端组', 'BACKEND', 3, '0,1,2', 2, 1, '后端开发小组');

-- 更新角色表的数据权限范围说明
ALTER TABLE `sys_role` MODIFY COLUMN `data_scope` tinyint DEFAULT '1' COMMENT '数据权限范围：1-全部数据，2-自定部门数据，3-本部门数据，4-本部门及以下数据，5-仅本人数据';

-- 插入部门管理相关菜单
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `is_external`, `is_cache`, `is_visible`, `status`, `remark`) VALUES
(1001, 1, '部门管理', 1, '/system/dept', 'pages/system/dept/DeptPage', 'system:dept:list', 'account_tree', 2, 0, 1, 1, 1, '部门管理菜单');

INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort_order`, `is_external`, `is_cache`, `is_visible`, `status`, `remark`) VALUES
(1002, 1001, '部门查询', 2, '', '', 'system:dept:query', '', 1, 0, 1, 1, 1, ''),
(1003, 1001, '部门新增', 2, '', '', 'system:dept:add', '', 2, 0, 1, 1, 1, ''),
(1004, 1001, '部门修改', 2, '', '', 'system:dept:edit', '', 3, 0, 1, 1, 1, ''),
(1005, 1001, '部门删除', 2, '', '', 'system:dept:remove', '', 4, 0, 1, 1, 1, '');

-- 为超级管理员角色分配部门管理权限（假设角色ID为1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1001), (1, 1002), (1, 1003), (1, 1004), (1, 1005);

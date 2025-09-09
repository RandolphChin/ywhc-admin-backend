-- 字典类型表
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL COMMENT '字典类型ID',
  `dict_name` varchar(100) NOT NULL COMMENT '字典名称',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1正常）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 字典数据表
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) NOT NULL COMMENT '字典键值',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认（1是 0否）',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（0停用 1正常）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建者',
  `update_by` bigint DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- 插入初始字典类型数据
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `remark`, `deleted`, `create_time`, `update_time`, `create_by`, `update_by`) VALUES
(1, '用户性别', 'sys_user_sex', 1, '用户性别列表', 0, NOW(), NOW(), 1, 1),
(2, '菜单状态', 'sys_show_hide', 1, '菜单状态列表', 0, NOW(), NOW(), 1, 1),
(3, '系统开关', 'sys_normal_disable', 1, '系统开关列表', 0, NOW(), NOW(), 1, 1),
(4, '任务状态', 'sys_job_status', 1, '任务状态列表', 0, NOW(), NOW(), 1, 1),
(5, '任务分组', 'sys_job_group', 1, '任务分组列表', 0, NOW(), NOW(), 1, 1),
(6, '系统是否', 'sys_yes_no', 1, '系统是否列表', 0, NOW(), NOW(), 1, 1),
(7, '通知类型', 'sys_notice_type', 1, '通知类型列表', 0, NOW(), NOW(), 1, 1),
(8, '通知状态', 'sys_notice_status', 1, '通知状态列表', 0, NOW(), NOW(), 1, 1),
(9, '操作类型', 'sys_oper_type', 1, '操作类型列表', 0, NOW(), NOW(), 1, 1),
(10, '系统状态', 'sys_common_status', 1, '登录状态列表', 0, NOW(), NOW(), 1, 1);

-- 插入初始字典数据
INSERT INTO `sys_dict_data` (`id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `remark`, `deleted`, `create_time`, `update_time`, `create_by`, `update_by`) VALUES
(1, 1, '男', '0', 'sys_user_sex', '', '', 1, 1, '性别男', 0, NOW(), NOW(), 1, 1),
(2, 2, '女', '1', 'sys_user_sex', '', '', 0, 1, '性别女', 0, NOW(), NOW(), 1, 1),
(3, 3, '未知', '2', 'sys_user_sex', '', '', 0, 1, '性别未知', 0, NOW(), NOW(), 1, 1),
(4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 1, 1, '显示菜单', 0, NOW(), NOW(), 1, 1),
(5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 0, 1, '隐藏菜单', 0, NOW(), NOW(), 1, 1),
(6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 1, 1, '正常状态', 0, NOW(), NOW(), 1, 1),
(7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 0, 1, '停用状态', 0, NOW(), NOW(), 1, 1),
(8, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 1, 1, '系统默认是', 0, NOW(), NOW(), 1, 1),
(9, 2, '否', 'N', 'sys_yes_no', '', 'danger', 0, 1, '系统默认否', 0, NOW(), NOW(), 1, 1),
(10, 1, '通知', '1', 'sys_notice_type', '', 'warning', 1, 1, '通知', 0, NOW(), NOW(), 1, 1),
(11, 2, '公告', '2', 'sys_notice_type', '', 'success', 0, 1, '公告', 0, NOW(), NOW(), 1, 1),
(12, 1, '正常', '0', 'sys_notice_status', '', 'primary', 1, 1, '正常状态', 0, NOW(), NOW(), 1, 1),
(13, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 0, 1, '关闭状态', 0, NOW(), NOW(), 1, 1),
(14, 1, '新增', '1', 'sys_oper_type', '', 'info', 0, 1, '新增操作', 0, NOW(), NOW(), 1, 1),
(15, 2, '修改', '2', 'sys_oper_type', '', 'info', 0, 1, '修改操作', 0, NOW(), NOW(), 1, 1),
(16, 3, '删除', '3', 'sys_oper_type', '', 'danger', 0, 1, '删除操作', 0, NOW(), NOW(), 1, 1),
(17, 4, '授权', '4', 'sys_oper_type', '', 'primary', 0, 1, '授权操作', 0, NOW(), NOW(), 1, 1),
(18, 5, '导出', '5', 'sys_oper_type', '', 'warning', 0, 1, '导出操作', 0, NOW(), NOW(), 1, 1),
(19, 6, '导入', '6', 'sys_oper_type', '', 'warning', 0, 1, '导入操作', 0, NOW(), NOW(), 1, 1),
(20, 7, '强退', '7', 'sys_oper_type', '', 'danger', 0, 1, '强退操作', 0, NOW(), NOW(), 1, 1),
(21, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 0, 1, '生成操作', 0, NOW(), NOW(), 1, 1),
(22, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 0, 1, '清空操作', 0, NOW(), NOW(), 1, 1),
(23, 1, '成功', '0', 'sys_common_status', '', 'primary', 0, 1, '正常状态', 0, NOW(), NOW(), 1, 1),
(24, 2, '失败', '1', 'sys_common_status', '', 'danger', 0, 1, '停用状态', 0, NOW(), NOW(), 1, 1);

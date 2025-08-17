package com.ywhc.admin.modules.system.menu.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单树VO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class MenuTreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型：0-目录，1-菜单，2-按钮
     */
    private Integer menuType;

    /**
     * 菜单类型描述
     */
    private String menuTypeDesc;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否外链：0-否，1-是
     */
    private Integer isExternal;

    /**
     * 是否缓存：0-否，1-是
     */
    private Integer isCache;

    /**
     * 是否显示：0-隐藏，1-显示
     */
    private Integer isVisible;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 子菜单列表
     */
    private List<MenuTreeVO> children;

    /**
     * 是否有子菜单
     */
    private Boolean hasChildren;
}

package com.ywhc.admin.modules.system.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单创建DTO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class MenuCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID
     */
    @NotNull(message = "父菜单ID不能为空")
    private Long parentId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /**
     * 菜单类型：0-目录，1-菜单，2-按钮
     */
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;

    /**
     * 路由路径
     */
    @Size(max = 200, message = "路由路径长度不能超过200个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String component;

    /**
     * 权限标识
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permission;

    /**
     * 图标
     */
    @Size(max = 50, message = "图标长度不能超过50个字符")
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
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}

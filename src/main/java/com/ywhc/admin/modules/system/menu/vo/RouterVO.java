package com.ywhc.admin.modules.system.menu.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路由VO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class RouterVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路由名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由元信息
     */
    private Meta meta;

    /**
     * 子路由
     */
    private List<RouterVO> children;

    @Data
    public static class Meta {
        /**
         * 菜单标题
         */
        private String title;

        /**
         * 菜单图标
         */
        private String icon;

        /**
         * 是否隐藏
         */
        private Boolean hidden;

        /**
         * 是否缓存
         */
        private Boolean keepAlive;

        /**
         * 权限标识
         */
        private String permission;

        /**
         * 是否外链
         */
        private Boolean isExternal;

        /**
         * 排序
         */
        private Integer sortOrder;
    }
}

package com.ywhc.admin.modules.auth.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户信息VO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 用户角色
     */
    private Set<String> roles;

    /**
     * 用户权限
     */
    private Set<String> permissions;

    /**
     * 用户菜单
     */
    private List<MenuInfo> menus;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 数据权限范围
     */
    private List<Long> dataScope;

    @Data
    public static class MenuInfo {
        private Long id;
        private Long parentId;
        private String menuName;
        private Integer menuType;
        private String path;
        private String component;
        private String permission;
        private String icon;
        private Integer sortOrder;
        private Integer isExternal;
        private Integer isCache;
        private Integer isVisible;
        private List<MenuInfo> children;
    }
}

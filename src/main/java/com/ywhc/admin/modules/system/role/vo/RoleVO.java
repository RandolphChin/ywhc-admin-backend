package com.ywhc.admin.modules.system.role.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 数据权限范围：1-全部数据，2-部门数据，3-部门及以下数据，4-仅本人数据
     */
    private Integer dataScope;

    /**
     * 数据权限描述
     */
    private String dataScopeDesc;

    /**
     * 排序
     */
    private Integer sortOrder;

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
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 菜单权限ID列表
     */
    private List<Long> menuIds;

    /**
     * 用户数量
     */
    private Long userCount;
}

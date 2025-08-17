package com.ywhc.admin.modules.system.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色创建DTO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class RoleCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    private String roleName;

    /**
     * 角色标识
     */
    @NotBlank(message = "角色标识不能为空")
    @Size(max = 50, message = "角色标识长度不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_:]+$", message = "角色标识只能包含字母、数字、下划线和冒号")
    private String roleKey;

    /**
     * 角色描述
     */
    @Size(max = 255, message = "角色描述长度不能超过255个字符")
    private String description;

    /**
     * 数据权限范围：1-全部数据，2-部门数据，3-部门及以下数据，4-仅本人数据
     */
    private Integer dataScope;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    /**
     * 菜单ID列表
     */
    private Long[] menuIds;
}

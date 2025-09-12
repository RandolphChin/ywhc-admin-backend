package com.ywhc.admin.modules.system.dept.dto;

import com.ywhc.admin.common.dto.BaseQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门查询DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptQueryDTO extends BaseQueryDTO {

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门类型
     */
    private Integer deptType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 父部门ID
     */
    private Long parentId;
}

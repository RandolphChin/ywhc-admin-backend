package com.ywhc.admin.modules.system.dept.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 部门保存DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class DeptSaveDTO {

    /**
     * 主键ID（更新时需要）
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 部门编码
     */
    @NotBlank(message = "部门编码不能为空")
    private String deptCode;

    /**
     * 部门类型：1-公司，2-部门，3-小组
     */
    @NotNull(message = "部门类型不能为空")
    private Integer deptType;

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
    private String remark;
}

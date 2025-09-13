package com.ywhc.admin.modules.system.dept.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "部门查询条件")
public class DeptQueryDTO extends BaseQueryDTO {

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    @QueryField(column = "dept_code", type = QueryType.EQUAL)
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

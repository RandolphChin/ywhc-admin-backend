package com.ywhc.admin.modules.system.role.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询条件")
public class RoleQueryDTO extends BaseQueryDTO {
    @QueryField(column = "role_name", type = QueryType.LIKE)
    private String roleName;
    @QueryField(column = "role_key", type = QueryType.LIKE)
    private String roleKey;
    @QueryField(column = "status", type = QueryType.EQUAL)
    private Integer status;
}

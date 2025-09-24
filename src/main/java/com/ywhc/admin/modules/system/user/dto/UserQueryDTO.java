package com.ywhc.admin.modules.system.user.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户查询DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询条件")
public class UserQueryDTO extends BaseQueryDTO {

    /**
     * 用户名
     */
    @QueryField(column = "user_name", type = QueryType.LIKE)
    private String username;

    /**
     * 状态：0-禁用，1-正常
     */
    @QueryField(column = "status", type = QueryType.EQUAL)
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;

}

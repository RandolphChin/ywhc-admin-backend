package com.ywhc.admin.modules.system.user.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @QueryField(column = "username", type = QueryType.LIKE)
    private String username;

    // 昵称
    @QueryField(column = "nickname", type = QueryType.LIKE)
    private String nickname;
    /**
     * 状态：0-禁用，1-正常
     */
    @QueryField(column = "status", type = QueryType.EQUAL)
    private Integer status;

    /**
     * 部门ID
     */
    @QueryField(column = "dept_id", type = QueryType.EQUAL)
    private Long deptId;

}

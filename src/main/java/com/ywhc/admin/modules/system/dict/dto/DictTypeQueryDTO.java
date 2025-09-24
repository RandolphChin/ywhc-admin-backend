package com.ywhc.admin.modules.system.dict.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型查询DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典类型查询DTO")
public class DictTypeQueryDTO extends BaseQueryDTO {
    @Schema(description = "字典名称（模糊查询）", example = "用户")
    @QueryField(column = "dict_name", type = QueryType.LIKE)
    private String dictNameLike;

    @Schema(description = "字典类型（模糊查询）", example = "sys_user")
    @QueryField(column = "dict_type", type = QueryType.LIKE)
    private String dictTypeLike;
}

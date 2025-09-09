package com.ywhc.admin.modules.system.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典类型查询DTO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "字典类型查询DTO")
public class DictTypeQueryDTO {

    @Schema(description = "当前页", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "排序字段", example = "createTime")
    private String orderBy = "createTime";

    @Schema(description = "排序方向", example = "desc")
    private String orderDirection = "desc";

    @Schema(description = "字典名称（模糊查询）", example = "用户")
    private String dictNameLike;

    @Schema(description = "字典类型（模糊查询）", example = "sys_user")
    private String dictTypeLike;

    @Schema(description = "状态：0-停用，1-正常", example = "1")
    private Integer status;
}

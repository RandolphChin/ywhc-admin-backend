package com.ywhc.admin.modules.system.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据查询DTO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "字典数据查询DTO")
public class DictDataQueryDTO {

    @Schema(description = "当前页", example = "1")
    private Integer current = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "排序字段", example = "dictSort")
    private String orderBy = "dictSort";

    @Schema(description = "排序方向", example = "asc")
    private String orderDirection = "asc";

    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;

    @Schema(description = "字典标签（模糊查询）", example = "男")
    private String dictLabelLike;

    @Schema(description = "字典键值（模糊查询）", example = "0")
    private String dictValueLike;

    @Schema(description = "状态：0-停用，1-正常", example = "1")
    private Integer status;
}

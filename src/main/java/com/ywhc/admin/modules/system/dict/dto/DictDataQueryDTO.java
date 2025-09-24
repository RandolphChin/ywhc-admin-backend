package com.ywhc.admin.modules.system.dict.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据查询DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典数据查询DTO")
public class DictDataQueryDTO extends BaseQueryDTO {
    @Schema(description = "字典类型", example = "sys_notice_status")
    @QueryField(column = "dict_type", type = QueryType.EQUAL)
    private String dictType;

    @Schema(description = "字典标签（模糊查询）", example = "男")
    @QueryField(column = "dict_label", type = QueryType.EQUAL)
    private String dictLabelLike;

    @Schema(description = "字典键值（模糊查询）", example = "0")
    @QueryField(column = "dict_value", type = QueryType.EQUAL)
    private String dictValueLike;
}

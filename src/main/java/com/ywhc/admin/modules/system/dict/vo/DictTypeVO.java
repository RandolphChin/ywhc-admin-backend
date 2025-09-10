package com.ywhc.admin.modules.system.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型VO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "字典类型VO")
public class DictTypeVO {

    @Schema(description = "字典类型ID")
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "备注")
    private String remark;
}

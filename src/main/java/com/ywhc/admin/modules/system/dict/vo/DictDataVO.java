package com.ywhc.admin.modules.system.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据VO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "字典数据VO")
public class DictDataVO {

    @Schema(description = "字典数据ID")
    private Long id;

    @Schema(description = "字典排序")
    private Integer dictSort;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    private String dictValue;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "备注")
    private String remark;

}

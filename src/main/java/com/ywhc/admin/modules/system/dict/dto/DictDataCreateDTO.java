package com.ywhc.admin.modules.system.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典数据创建DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "字典数据创建DTO")
public class DictDataCreateDTO {

    @Schema(description = "字典排序", example = "1")
    private Integer dictSort = 0;

    @NotBlank(message = "字典标签不能为空")
    @Schema(description = "字典标签", example = "男")
    private String dictLabel;

    @NotBlank(message = "字典键值不能为空")
    @Schema(description = "字典键值", example = "0")
    private String dictValue;

    @NotBlank(message = "字典类型不能为空")
    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;

    @Schema(description = "备注", example = "性别男")
    private String remark;
}

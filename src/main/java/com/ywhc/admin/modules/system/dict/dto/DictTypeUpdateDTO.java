package com.ywhc.admin.modules.system.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典类型更新DTO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "字典类型更新DTO")
public class DictTypeUpdateDTO {

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "字典类型ID", example = "1")
    private Long id;

    @NotBlank(message = "字典名称不能为空")
    @Schema(description = "字典名称", example = "用户性别")
    private String dictName;

    @NotBlank(message = "字典类型不能为空")
    @Schema(description = "字典类型", example = "sys_user_sex")
    private String dictType;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-停用，1-正常", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "用户性别列表")
    private String remark;
}

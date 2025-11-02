package com.ywhc.admin.modules.generator.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.ywhc.admin.common.dto.BaseQueryDTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "GeneratorQueryDTO", description = "代码生成查询DTO")
public class GeneratorQueryDTO extends BaseQueryDTO {

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "表注释")
    private String tableComment;

}

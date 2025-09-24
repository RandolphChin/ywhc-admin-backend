package ${package.Parent}.dto;

import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

<#list table.importPackages as pkg>
  <#if pkg?contains("LocalDate") || pkg?contains("LocalDateTime")>
import ${pkg};
  </#if>
</#list>

/**
 * ${table.comment!}查询DTO
 *
 * @author ${author}
 * @since ${date}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "${entity}QueryDTO", description = "${table.comment!}查询DTO")
public class ${entity}QueryDTO extends BaseQueryDTO {

<#list table.fields as field>
  <#if !field.keyFlag && field.propertyName != "createTime" && field.propertyName != "updateTime">
    <#if field.comment!?length gt 0>
    @Schema(description = "${field.comment}")
    </#if>
    private ${field.propertyType} ${field.propertyName};

    <#if field.propertyType == "String">
    <#if field.comment!?length gt 0>
    @Schema(description = "${field.comment}(模糊查询)")
    </#if>
    private ${field.propertyType} ${field.propertyName}Like;

    </#if>
  </#if>
</#list>
    
    /**
     * 创建时间范围查询
     */
    @Schema(description = "创建时间范围")
    private String[] createTimeBetween;
    
    /**
     * 更新时间范围查询
     */
    @Schema(description = "更新时间范围")
    private String[] updateTimeBetween;
}
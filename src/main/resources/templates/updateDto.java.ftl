package ${package.Parent}.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

<#list table.importPackages as pkg>
  <#if pkg?contains("LocalDate") || pkg?contains("LocalDateTime") || pkg?contains("BigDecimal")>
import ${pkg};
  </#if>
</#list>

/**
 * ${table.comment!}修改DTO
 *
 * @author ${author}
 * @since ${date}
 */
@Data
@Schema(name = "${entity}UpdateDTO", description = "${table.comment!}修改DTO")
public class ${entity}UpdateDTO {

<#list table.fields as field>
  <#if field.keyFlag>
    <#-- 主键字段，更新时必需 -->
    <#if field.comment!?length gt 0>
    @Schema(description = "${field.comment}")
    </#if>
    @NotNull(message = "${field.comment!'主键'}不能为空")
    private ${field.propertyType} ${field.propertyName};

  <#elseif field.propertyName != "createTime" && field.propertyName != "updateTime" && field.propertyName != "createBy" && field.propertyName != "updateBy">
    <#-- 普通字段，修改操作的校验相对宽松，支持部分更新，只校验长度 -->
    <#if field.comment!?length gt 0>
    @Schema(description = "${field.comment}")
    </#if>
    <#if field.propertyType == "String" && field.columnLength?? && field.columnLength gt 0>
    @Size(max = ${field.columnLength?c}, message = "${field.comment!'该字段'}长度不能超过${field.columnLength?c}个字符")
    </#if>
    private ${field.propertyType} ${field.propertyName};

  </#if>
</#list>
}

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
 * ${table.comment!}新增DTO
 *
 * @author ${author}
 * @since ${date}
 */
@Data
@Schema(name = "${entity}CreateDTO", description = "${table.comment!}新增DTO")
public class ${entity}CreateDTO {

<#list table.fields as field>
  <#if !field.keyFlag && field.propertyName != "createTime" && field.propertyName != "updateTime" && field.propertyName != "createBy" && field.propertyName != "updateBy">
    <#if field.comment!?length gt 0>
    @Schema(description = "${field.comment}")
    </#if>
    <#-- 根据数据库字段约束添加校验注解 -->
    <#if !field.nullable>
      <#if field.propertyType == "String">
    @NotBlank(message = "${field.comment!'该字段'}不能为空")
      <#else>
    @NotNull(message = "${field.comment!'该字段'}不能为空")
      </#if>
    </#if>
    <#if field.propertyType == "String" && field.columnLength?? && field.columnLength gt 0>
    @Size(max = ${field.columnLength?c}, message = "${field.comment!'该字段'}长度不能超过${field.columnLength?c}个字符")
    </#if>
    private ${field.propertyType} ${field.propertyName};

  </#if>
</#list>
}

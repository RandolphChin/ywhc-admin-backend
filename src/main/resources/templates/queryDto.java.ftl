package ${package.Parent}.dto;

import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;
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
    @QueryField(column = "${field.columnName}", type = QueryType.LIKE)
    private ${field.propertyType} ${field.propertyName};

  </#if>
</#list>
    
<#-- 为所有LocalDateTime和LocalDate类型字段生成时间范围查询 -->
<#list table.fields as field>
  <#if field.propertyType == "LocalDateTime" || field.propertyType == "LocalDate">
    /**
     * ${field.comment!'时间'}范围查询
     */
    @Schema(description = "${field.comment!'时间'}范围")
    <#if field.propertyType == "LocalDateTime">
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") // 用于URL参数
    <#else>
    @DateTimeFormat(pattern="yyyy-MM-dd") // 用于URL参数
    </#if>
    @QueryField(column = "${field.columnName}", type = QueryType.DATE_RANGE)
    private List<${field.propertyType}> ${field.propertyName}Between;

  </#if>
</#list>
    
}

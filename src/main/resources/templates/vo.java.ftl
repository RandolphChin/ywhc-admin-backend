package ${package.Parent}.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

<#list table.importPackages as pkg>
import ${pkg};
</#list>

/**
 * ${table.comment!}VO
 *
 * @author ${author}
 * @since ${date}
 */
@Data
@Schema(name = "${entity}VO", description = "${table.comment!}VO")
public class ${entity}VO {

<#list table.fields as field>
    <#if field.comment!?length gt 0>
    @Schema(description = "${field.comment}")
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#list>
}
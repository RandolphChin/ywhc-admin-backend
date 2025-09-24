<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
<#list table.fields as field>
  <#if field.keyFlag>
        <id column="${field.columnName}" property="${field.propertyName}" />
  <#else>
        <result column="${field.columnName}" property="${field.propertyName}" />
  </#if>
</#list>
    </resultMap>

    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
<#list table.fields as field>
        ${field.columnName}<#if field_has_next>,</#if>
</#list>
    </sql>

</mapper>
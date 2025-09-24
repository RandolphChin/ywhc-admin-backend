package com.ywhc.admin.modules.generator.entity;

import lombok.Data;

/**
 * 数据库字段信息
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class ColumnInfo {
    
    /**
     * 字段名
     */
    private String columnName;
    
    /**
     * 字段类型
     */
    private String dataType;
    
    /**
     * 字段注释
     */
    private String columnComment;
    
    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;
    
    /**
     * 是否自增
     */
    private Boolean isAutoIncrement;
    
    /**
     * 是否可为空
     */
    private Boolean isNullable;
    
    /**
     * 字段长度
     */
    private Long columnLength;
    
    /**
     * 默认值
     */
    private String columnDefault;
    
    /**
     * Java类型
     */
    private String javaType;
    
    /**
     * Java字段名
     */
    private String javaField;
}
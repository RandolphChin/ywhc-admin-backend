package com.ywhc.admin.modules.generator.entity;

import lombok.Data;

import java.util.List;

/**
 * 数据库表信息
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class TableInfo {
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 表注释
     */
    private String tableComment;
    
    /**
     * 表引擎
     */
    private String engine;
    
    /**
     * 创建时间
     */
    private String createTime;
    
    /**
     * 字段列表
     */
    private List<ColumnInfo> columns;
}
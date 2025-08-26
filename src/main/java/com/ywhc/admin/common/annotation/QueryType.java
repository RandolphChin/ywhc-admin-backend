package com.ywhc.admin.common.annotation;

/**
 * 查询类型枚举
 * 定义不同的查询条件类型
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public enum QueryType {
    
    /**
     * 等于查询 (=)
     */
    EQUAL,
    
    /**
     * 不等于查询 (!=)
     */
    NOT_EQUAL,
    
    /**
     * 模糊查询 (LIKE %value%)
     */
    LIKE,
    
    /**
     * 左模糊查询 (LIKE %value)
     */
    LEFT_LIKE,
    
    /**
     * 右模糊查询 (LIKE value%)
     */
    RIGHT_LIKE,
    
    /**
     * 大于查询 (>)
     */
    GREATER_THAN,
    
    /**
     * 大于等于查询 (>=)
     */
    GREATER_EQUAL,
    
    /**
     * 小于查询 (<)
     */
    LESS_THAN,
    
    /**
     * 小于等于查询 (<=)
     */
    LESS_EQUAL,
    
    /**
     * 范围查询 (BETWEEN)
     * 字段值应为数组或集合，包含开始和结束值
     */
    BETWEEN,
    
    /**
     * IN查询 (IN)
     * 字段值应为数组或集合
     */
    IN,
    
    /**
     * NOT IN查询 (NOT IN)
     * 字段值应为数组或集合
     */
    NOT_IN,
    
    /**
     * 为空查询 (IS NULL)
     */
    IS_NULL,
    
    /**
     * 不为空查询 (IS NOT NULL)
     */
    IS_NOT_NULL,
    
    /**
     * 日期范围查询
     * 特殊处理日期范围，字段值应为日期范围对象
     */
    DATE_RANGE,
    
    /**
     * 自定义查询
     * 需要在QueryProcessor中自定义处理逻辑
     */
    CUSTOM
}

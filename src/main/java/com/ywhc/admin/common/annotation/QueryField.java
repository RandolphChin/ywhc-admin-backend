package com.ywhc.admin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询字段注解
 * 用于标识DTO字段的查询类型和对应的数据库字段
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryField {
    
    /**
     * 数据库字段名，默认使用字段名的下划线形式
     */
    String column() default "";
    
    /**
     * 查询类型
     */
    QueryType type() default QueryType.EQUAL;
    
    /**
     * 是否忽略空值，默认忽略
     */
    boolean ignoreEmpty() default true;
    
    /**
     * 日期格式，当字段为日期类型时使用
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 排序优先级，数字越小优先级越高，-1表示不参与排序
     */
    int sortOrder() default -1;
    
    /**
     * 是否为排序字段
     */
    boolean sortable() default false;
}

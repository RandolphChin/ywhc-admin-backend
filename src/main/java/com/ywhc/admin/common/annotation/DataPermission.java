package com.ywhc.admin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * 用于标记需要进行数据权限控制的方法
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {
    
    /**
     * 部门表别名
     */
    String deptAlias() default "";
    
    /**
     * 部门字段名
     */
    String deptIdColumn() default "dept_id";
    
    /**
     * 用户表别名
     */
    String userAlias() default "";
    
    /**
     * 用户字段名
     */
    String userIdColumn() default "create_by";
    
    /**
     * 是否过滤用户数据
     */
    boolean filterUser() default true;
    
    /**
     * 是否过滤部门数据
     */
    boolean filterDept() default true;
}

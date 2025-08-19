package com.ywhc.admin.modules.system.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志记录注解
 * 用于标记需要记录操作日志的Controller方法
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAccess {
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 操作类型：1-新增，2-修改，3-删除，4-查询，5-登录，6-登出
     */
    int operationType() default 4;
}

package com.ywhc.admin.common.annotation;

import com.ywhc.admin.common.enums.OperationType;
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
     * 操作类型
     */
    OperationType operationType() default OperationType.QUERY;
}

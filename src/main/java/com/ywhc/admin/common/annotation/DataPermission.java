package com.ywhc.admin.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * 用于标记需要进行数据权限控制的方法
 *
 * 根据 sys_role 表中的 data_scope 字段：

data_scope	权限范围	生成的SQL条件
1	全部数据	无额外条件
2	自定部门数据	dept_id IN (授权部门列表)
3	本部门数据	dept_id = 用户部门ID
4	本部门及以下数据	dept_id IN (本部门+子部门列表)
5	仅本人数据	user_id = 当前用户ID


1. 单表查询时的 @DataPermission 配置
不设置别名，deptAlias 不设置
@DataPermission(deptIdColumn = "dept_id", userIdColumn = "user_id")

@DataPermission(deptAlias = "sl", userAlias = "sl", deptIdColumn = "dept_id", userIdColumn = "user_id")
生成的SQL条件：WHERE (sl.dept_id IN (2,5,6) OR sl.user_id = 1001)


 * @author YWHC Team
 * @since 2024-01-01
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {

    /**
     * 部门表别名 （联表查询 LEFT JOIN sys_dept 别名）,查询SQL中没有 JOIN 不使用
     */
    String deptAlias() default "";

    /**
     * 部门字段名（业务表中使用哪个字段，对应 sys_dept中的主键），按照部门级数据过滤
     */
    String deptIdColumn() default "dept_id";

    /**
     * 用户表别名   （联表查询 LEFT JOIN sys_user 别名）,查询SQL中没有 JOIN 不使用
     */
    String userAlias() default "";

    /**
     * 用户字段名（业务表中使用哪个字段，对应sys_user中的主键）
     */
    String userIdColumn() default "";

    /**
     * 是否过滤用户数据
     */
    boolean filterUser() default true;

    /**
     * 是否过滤部门数据
     */
    boolean filterDept() default true;
}

package com.ywhc.admin.modules.generator.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * GeneratorUtils测试类
 */
public class GeneratorUtilsTest {

    @Test
    public void testToPascalCase() {
        // 测试下划线转帕斯卡命名
        assertEquals("EnterpriseName", GeneratorUtils.toPascalCase("enterprise_name"));
        assertEquals("EnterpriseAddress", GeneratorUtils.toPascalCase("enterprise_address"));
        assertEquals("DeptId", GeneratorUtils.toPascalCase("dept_id"));
        assertEquals("CreateTime", GeneratorUtils.toPascalCase("create_time"));
        assertEquals("UpdateTime", GeneratorUtils.toPascalCase("update_time"));
        assertEquals("CreateBy", GeneratorUtils.toPascalCase("create_by"));
        assertEquals("UpdateBy", GeneratorUtils.toPascalCase("update_by"));
        
        // 测试已经是驼峰格式的字符串
        assertEquals("EnterpriseName", GeneratorUtils.toPascalCase("enterpriseName"));
        assertEquals("EnterpriseAddress", GeneratorUtils.toPascalCase("enterpriseAddress"));
        assertEquals("DeptId", GeneratorUtils.toPascalCase("deptId"));
        assertEquals("CreateTime", GeneratorUtils.toPascalCase("createTime"));
        assertEquals("UpdateTime", GeneratorUtils.toPascalCase("updateTime"));
        assertEquals("CreateBy", GeneratorUtils.toPascalCase("createBy"));
        assertEquals("UpdateBy", GeneratorUtils.toPascalCase("updateBy"));
        
        // 测试边界情况
        assertEquals("", GeneratorUtils.toPascalCase(""));
        assertEquals("A", GeneratorUtils.toPascalCase("a"));
        assertEquals("Id", GeneratorUtils.toPascalCase("id"));
    }

    @Test
    public void testToCamelCase() {
        // 测试下划线转驼峰
        assertEquals("enterpriseName", GeneratorUtils.toCamelCase("enterprise_name"));
        assertEquals("enterpriseAddress", GeneratorUtils.toCamelCase("enterprise_address"));
        assertEquals("deptId", GeneratorUtils.toCamelCase("dept_id"));
        
        // 测试已经是驼峰格式的字符串
        assertEquals("enterpriseName", GeneratorUtils.toCamelCase("enterpriseName"));
        assertEquals("enterpriseAddress", GeneratorUtils.toCamelCase("enterpriseAddress"));
        assertEquals("deptId", GeneratorUtils.toCamelCase("deptId"));
        
        // 测试边界情况
        assertEquals("", GeneratorUtils.toCamelCase(""));
        assertEquals("a", GeneratorUtils.toCamelCase("a"));
        assertEquals("id", GeneratorUtils.toCamelCase("id"));
    }
}

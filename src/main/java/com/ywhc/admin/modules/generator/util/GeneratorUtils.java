package com.ywhc.admin.modules.generator.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成工具类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public class GeneratorUtils {
    
    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();
    
    static {
        TYPE_MAPPING.put("tinyint", "Integer");
        TYPE_MAPPING.put("smallint", "Integer");
        TYPE_MAPPING.put("mediumint", "Integer");
        TYPE_MAPPING.put("int", "Integer");
        TYPE_MAPPING.put("integer", "Integer");
        TYPE_MAPPING.put("bigint", "Long");
        TYPE_MAPPING.put("float", "Float");
        TYPE_MAPPING.put("double", "Double");
        TYPE_MAPPING.put("decimal", "BigDecimal");
        TYPE_MAPPING.put("bit", "Boolean");
        TYPE_MAPPING.put("char", "String");
        TYPE_MAPPING.put("varchar", "String");
        TYPE_MAPPING.put("tinytext", "String");
        TYPE_MAPPING.put("text", "String");
        TYPE_MAPPING.put("mediumtext", "String");
        TYPE_MAPPING.put("longtext", "String");
        TYPE_MAPPING.put("date", "LocalDate");
        TYPE_MAPPING.put("datetime", "LocalDateTime");
        TYPE_MAPPING.put("timestamp", "LocalDateTime");
        TYPE_MAPPING.put("time", "LocalTime");
        TYPE_MAPPING.put("year", "Integer");
        TYPE_MAPPING.put("binary", "byte[]");
        TYPE_MAPPING.put("varbinary", "byte[]");
        TYPE_MAPPING.put("tinyblob", "byte[]");
        TYPE_MAPPING.put("blob", "byte[]");
        TYPE_MAPPING.put("mediumblob", "byte[]");
        TYPE_MAPPING.put("longblob", "byte[]");
        TYPE_MAPPING.put("json", "String");
    }
    
    /**
     * 获取Java类型
     */
    public static String getJavaType(String dbType) {
        return TYPE_MAPPING.getOrDefault(dbType.toLowerCase(), "String");
    }
    
    /**
     * 下划线转驼峰
     */
    public static String toCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 转换为帕斯卡命名法（首字母大写的驼峰）
     */
    public static String toPascalCase(String str) {
        String camelCase = toCamelCase(str);
        if (camelCase.isEmpty()) {
            return camelCase;
        }
        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }
    
    /**
     * 驼峰转下划线
     */
    public static String toUnderlineCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * 获取Vue组件名
     */
    public static String getVueComponentName(String businessName) {
        return toPascalCase(businessName) + "Page";
    }
    
    /**
     * 获取API文件名
     */
    public static String getApiFileName(String businessName) {
        return toCamelCase(businessName) + ".js";
    }
}
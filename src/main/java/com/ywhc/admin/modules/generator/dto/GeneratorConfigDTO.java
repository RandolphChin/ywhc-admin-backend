package com.ywhc.admin.modules.generator.dto;

import lombok.Data;

import java.util.List;

/**
 * 代码生成配置DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class GeneratorConfigDTO {
    
    /**
     * 表名
     */
    private String tableName;
    
    /**
     * 模块名
     */
    private String moduleName;
    
    /**
     * 业务名
     */
    private String businessName;
    
    /**
     * 功能名
     */
    private String functionName;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 包名
     */
    private String packageName;
    
    /**
     * 生成选项
     */
    private GenerateOptions generateOptions;
    
    @Data
    public static class GenerateOptions {
        /**
         * 生成Controller
         */
        private Boolean generateController = true;
        
        /**
         * 生成Service
         */
        private Boolean generateService = true;
        
        /**
         * 生成ServiceImpl
         */
        private Boolean generateServiceImpl = true;
        
        /**
         * 生成Mapper
         */
        private Boolean generateMapper = true;
        
        /**
         * 生成MapperXML
         */
        private Boolean generateMapperXml = true;
        
        /**
         * 生成Entity
         */
        private Boolean generateEntity = true;
        
        /**
         * 生成DTO
         */
        private Boolean generateDto = true;
        
        /**
         * 生成VO
         */
        private Boolean generateVo = true;
        
        /**
         * 生成Vue页面
         */
        private Boolean generateVuePage = true;
        
        /**
         * 生成Vue API
         */
        private Boolean generateVueApi = true;
        
        /**
         * 生成菜单SQL
         */
        private Boolean generateMenuSql = true;
        
        /**
         * 生成权限SQL
         */
        private Boolean generatePermissionSql = true;
    }
}
package com.ywhc.admin.modules.generator.vo;

import lombok.Data;

import java.util.Map;

/**
 * 生成的代码VO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class GeneratedCodeVO {
    
    /**
     * 生成的文件内容 Map<文件路径, 文件内容>
     */
    private Map<String, String> files;
    
    /**
     * 菜单SQL
     */
    private String menuSql;
    
    /**
     * 权限SQL
     */
    private String permissionSql;
    
    /**
     * 生成时间
     */
    private String generateTime;
}
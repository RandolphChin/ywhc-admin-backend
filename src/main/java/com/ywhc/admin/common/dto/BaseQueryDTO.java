package com.ywhc.admin.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基础查询DTO
 * 包含分页和排序的通用字段
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "基础查询条件")
public class BaseQueryDTO {
    
    @Schema(description = "当前页", example = "1")
    private Long current = 1L;
    
    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;
    
    @Schema(description = "排序字段")
    private String orderBy;
    
    @Schema(description = "排序方向 - asc/desc")
    private String orderDirection = "desc";
    
    /**
     * 获取排序SQL片段
     */
    public String getOrderSql() {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return null;
        }
        
        String direction = "desc".equalsIgnoreCase(orderDirection) ? "DESC" : "ASC";
        // 将驼峰转换为下划线
        String column = camelToUnderscore(orderBy.trim());
        return column + " " + direction;
    }
    
    /**
     * 驼峰命名转下划线命名
     */
    private String camelToUnderscore(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}

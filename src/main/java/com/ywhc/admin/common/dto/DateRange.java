package com.ywhc.admin.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日期范围DTO
 * 用于处理日期范围查询
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "日期范围")
public class DateRange {
    
    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @Schema(description = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    public DateRange() {}
    
    public DateRange(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    /**
     * 检查日期范围是否有效
     */
    public boolean isValid() {
        return startTime != null && endTime != null && !startTime.isAfter(endTime);
    }
    
    /**
     * 检查是否为空范围
     */
    public boolean isEmpty() {
        return startTime == null && endTime == null;
    }
}

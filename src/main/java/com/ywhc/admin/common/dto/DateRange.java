package com.ywhc.admin.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 日期范围DTO
 * 用于处理日期范围查询
 * 使用LocalDateTime接收，前端必须传 yyyy-MM-dd HH:mm:ss格式
 * 使用Date接收，前端必须传 yyyy-MM-dd格式
 *
 * **@JsonFormat**：
 * - 专门用于 JSON 序列化/反序列化
 * - 由 Jackson 库处理
 * - 适用于 `@RequestBody` 的 JSON 数据
 *
 * **@DateTimeFormat**：
 * - 用于 HTTP 请求参数绑定
 * - 由 Spring MVC 数据绑定机制处理
 * - 适用于 URL 参数、表单参数等
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "日期范围")
public class DateRange {

    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd[ HH:mm:ss]") // 用于JSON序列化（这里用不上）
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") // 用于URL参数
    private LocalDateTime startTime; // 使用LocalDateTime前端必须传 yyyy-MM-dd HH:mm:ss格式

    @Schema(description = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd[ HH:mm:ss]")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
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

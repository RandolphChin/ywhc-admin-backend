package com.ywhc.admin.modules.system.log.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;
import com.ywhc.admin.common.dto.DateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志查询DTO
 * 使用注解定义查询条件类型
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "日志查询条件")
public class LogQueryDTO extends BaseQueryDTO {

    @Schema(description = "操作用户 - 精确匹配")
    @QueryField(column = "username", type = QueryType.EQUAL)
    private String username;

    @Schema(description = "操作用户 - 模糊查询")
    @QueryField(column = "username", type = QueryType.LIKE)
    private String usernameLike;

    @Schema(description = "操作描述 - 模糊查询")
    @QueryField(column = "operation_desc", type = QueryType.LIKE)
    private String operationDescLike;


    @Schema(description = "请求方法 - 精确匹配")
    @QueryField(column = "request_method", type = QueryType.EQUAL)
    private String requestMethod;

    @Schema(description = "请求方法 - IN查询")
    @QueryField(column = "request_method", type = QueryType.IN)
    private List<String> requestMethods;

    @Schema(description = "操作状态 - 精确匹配")
    @QueryField(column = "status", type = QueryType.EQUAL)
    private Integer status;


    @Schema(description = "操作状态 - 不等于查询")
    @QueryField(column = "status", type = QueryType.NOT_EQUAL)
    private Integer statusNotEqual;

    @Schema(description = "操作时间范围")
    @QueryField(column = "create_time", type = QueryType.DATE_RANGE)
    private DateRange createTimeRange;

    @Schema(description = "操作类型 - IN查询")
    @QueryField(column = "operation_type", type = QueryType.IN)
    private List<Integer> operationTypes;

    @Schema(description = "排除的用户ID")
    @QueryField(column = "user_id", type = QueryType.NOT_IN)
    private List<Long> excludeUserIds;

    @Schema(description = "是否有错误信息")
    @QueryField(column = "error_msg", type = QueryType.IS_NOT_NULL)
    private Boolean hasError;

}

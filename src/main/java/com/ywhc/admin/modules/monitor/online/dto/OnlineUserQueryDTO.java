package com.ywhc.admin.modules.monitor.online.dto;

import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import com.ywhc.admin.common.dto.BaseQueryDTO;
import com.ywhc.admin.common.dto.DateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 在线用户查询DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "在线用户查询条件")
public class OnlineUserQueryDTO extends BaseQueryDTO {

    @Schema(description = "用户名 - 精确匹配")
    @QueryField(column = "username", type = QueryType.EQUAL)
    private String username;

    @Schema(description = "用户名 - 模糊查询")
    @QueryField(column = "username", type = QueryType.LIKE)
    private String usernameLike;

    @Schema(description = "昵称 - 模糊查询")
    @QueryField(column = "nickname", type = QueryType.LIKE)
    private String nicknameLike;

    @Schema(description = "登录IP地址 - 精确匹配")
    @QueryField(column = "ip_address", type = QueryType.EQUAL)
    private String ipAddress;

    @Schema(description = "登录IP地址 - 模糊查询")
    @QueryField(column = "ip_address", type = QueryType.LIKE)
    private String ipAddressLike;

    @Schema(description = "登录地点 - 模糊查询")
    @QueryField(column = "location", type = QueryType.LIKE)
    private String locationLike;

    @Schema(description = "浏览器 - 模糊查询")
    @QueryField(column = "browser", type = QueryType.LIKE)
    private String browserLike;

    @Schema(description = "操作系统 - 模糊查询")
    @QueryField(column = "os", type = QueryType.LIKE)
    private String osLike;

    @Schema(description = "会话状态 - 精确匹配")
    @QueryField(column = "status", type = QueryType.EQUAL)
    private Integer status;

    @Schema(description = "设备类型 - 精确匹配")
    @QueryField(column = "device_type", type = QueryType.EQUAL)
    private Integer deviceType;

    @Schema(description = "登录时间范围")
    @QueryField(column = "login_time", type = QueryType.DATE_RANGE)
    private DateRange loginTimeRange;

    @Schema(description = "最后活动时间范围")
    @QueryField(column = "last_access_time", type = QueryType.DATE_RANGE)
    private DateRange lastAccessTimeRange;
}

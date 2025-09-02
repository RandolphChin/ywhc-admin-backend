package com.ywhc.admin.modules.monitor.online.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户VO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "在线用户信息")
public class OnlineUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "访问Token")
    private String accessToken;

    @Schema(description = "登录IP地址")
    private String ipAddress;

    @Schema(description = "登录地点")
    private String location;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    @Schema(description = "最后活动时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime;

    @Schema(description = "Token过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "会话状态：1-在线，0-离线")
    private Integer status;

    @Schema(description = "会话状态描述")
    private String statusDesc;

    @Schema(description = "设备类型：1-PC，2-移动端")
    private Integer deviceType;

    @Schema(description = "设备类型描述")
    private String deviceTypeDesc;

    @Schema(description = "在线时长（分钟）")
    private Long onlineDuration;

    @Schema(description = "备注")
    private String remark;
}

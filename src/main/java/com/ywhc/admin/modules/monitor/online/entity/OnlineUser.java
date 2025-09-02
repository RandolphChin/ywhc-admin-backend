package com.ywhc.admin.modules.monitor.online.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户实体类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class OnlineUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 访问Token
     */
    private String accessToken;

    /**
     * 刷新Token
     */
    private String refreshToken;

    /**
     * 登录IP地址
     */
    private String ipAddress;

    /**
     * 登录地点
     */
    private String location;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    /**
     * 最后活动时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAccessTime;

    /**
     * Token过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 会话状态：1-在线，0-离线
     */
    private Integer status;

    /**
     * 设备类型：1-PC，2-移动端
     */
    private Integer deviceType;

    /**
     * 备注
     */
    private String remark;
}

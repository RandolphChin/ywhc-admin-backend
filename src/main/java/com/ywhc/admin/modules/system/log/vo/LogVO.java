package com.ywhc.admin.modules.system.log.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class LogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型：1-新增，2-修改，3-删除，4-查询，5-登录，6-登出
     */
    private Integer operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 执行时间(毫秒)
     */
    private Long executionTime;

    /**
     * 操作状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 操作IP
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 创建时间
     */
    @Schema(description = "创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd[ HH:mm:ss]") // 用于JSON序列化
    private LocalDateTime createTime;
}

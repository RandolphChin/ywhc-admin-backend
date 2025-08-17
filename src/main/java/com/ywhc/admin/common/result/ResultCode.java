package com.ywhc.admin.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态码枚举
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    VALIDATION_ERROR(422, "参数校验失败"),

    // 服务端错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    USERNAME_EXISTS(1003, "用户名已存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    OLD_PASSWORD_ERROR(1005, "原密码错误"),

    ROLE_NOT_FOUND(2001, "角色不存在"),
    ROLE_EXISTS(2002, "角色已存在"),
    ROLE_HAS_USERS(2003, "角色下存在用户，无法删除"),

    MENU_NOT_FOUND(3001, "菜单不存在"),
    MENU_HAS_CHILDREN(3002, "菜单下存在子菜单，无法删除"),

    TOKEN_INVALID(4001, "Token无效"),
    TOKEN_EXPIRED(4002, "Token已过期"),
    TOKEN_MISSING(4003, "Token缺失"),
    REFRESH_TOKEN_INVALID(4004, "刷新Token无效"),

    FILE_UPLOAD_ERROR(5001, "文件上传失败"),
    FILE_TYPE_ERROR(5002, "文件类型不支持"),
    FILE_SIZE_ERROR(5003, "文件大小超出限制");

    private final Integer code;
    private final String message;
}

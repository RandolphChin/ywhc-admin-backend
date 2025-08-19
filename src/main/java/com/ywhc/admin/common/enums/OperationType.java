package com.ywhc.admin.common.enums;

/**
 * 操作类型枚举
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public enum OperationType {
    /**
     * 新增操作
     */
    CREATE(1, "新增"),

    /**
     * 修改操作
     */
    UPDATE(2, "修改"),

    /**
     * 删除操作
     */
    DELETE(3, "删除"),

    /**
     * 查询操作
     */
    QUERY(4, "查询"),

    /**
     * 登录操作
     */
    LOGIN(5, "登录"),

    /**
     * 登出操作
     */
    LOGOUT(6, "登出");

    private final int code;
    private final String description;

    OperationType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取操作类型
     */
    public static OperationType fromCode(int code) {
        for (OperationType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown operation type code: " + code);
    }
}

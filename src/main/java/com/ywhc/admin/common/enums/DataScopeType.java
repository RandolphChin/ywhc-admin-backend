package com.ywhc.admin.common.enums;

/**
 * 数据权限范围类型枚举
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public enum DataScopeType {
    /**
     * 全部数据权限
     */
    ALL_DATA(1, "全部数据权限"),

    /**
     * 自定部门数据权限
     */
    CUSTOM_DEPT(2, "自定部门数据权限"),

    /**
     * 本部门数据权限
     */
    CURRENT_DEPT(3, "本部门数据权限"),

    /**
     * 本部门及以下数据权限
     */
    CURRENT_AND_SUB_DEPT(4, "本部门及以下数据权限"),

    /**
     * 仅本人数据权限
     */
    SELF_ONLY(5, "仅本人数据权限");

    private final int code;
    private final String description;

    DataScopeType(int code, String description) {
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
     * 根据代码获取数据权限范围类型
     */
    public static DataScopeType fromCode(int code) {
        for (DataScopeType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown data scope type code: " + code);
    }
}

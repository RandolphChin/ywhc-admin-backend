package com.ywhc.admin.common.context;

/**
 * 数据权限上下文持有者
 * 用于在ThreadLocal中存储数据权限SQL片段
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public class DataScopeContextHolder {
    
    private static final ThreadLocal<String> DATA_SCOPE_CONTEXT = new ThreadLocal<>();
    
    /**
     * 设置数据权限SQL片段
     * 
     * @param dataScope SQL片段
     */
    public static void setDataScope(String dataScope) {
        DATA_SCOPE_CONTEXT.set(dataScope);
    }
    
    /**
     * 获取数据权限SQL片段
     * 
     * @return SQL片段
     */
    public static String getDataScope() {
        return DATA_SCOPE_CONTEXT.get();
    }
    
    /**
     * 清除数据权限上下文
     */
    public static void clear() {
        DATA_SCOPE_CONTEXT.remove();
    }
}

package com.ywhc.admin.common.aspect;

import com.ywhc.admin.common.annotation.DataPermission;
import com.ywhc.admin.common.context.DataScopeContextHolder;
import com.ywhc.admin.common.util.SecurityUtils;
import com.ywhc.admin.modules.system.dept.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限切面
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DataPermissionAspect {

    private final SysDeptService deptService;

    /**
     * 数据权限处理
     */
    @Before("@annotation(dataPermission)")
    public void doBefore(JoinPoint point, DataPermission dataPermission) {
        clearDataScope();
        handleDataScope(dataPermission);
    }

    /**
     * 处理数据权限
     */
    protected void handleDataScope(DataPermission dataPermission) {
        // 获取当前用户
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return;
        }

        // 获取用户数据权限范围
        Set<Long> dataScope = deptService.getDataScope(userId);
        
        if (dataScope != null && !dataScope.isEmpty()) {
            // 构建数据权限SQL片段
            StringBuilder sqlString = new StringBuilder();
            
            if (dataPermission.filterDept()) {
                String deptAlias = dataPermission.deptAlias();
                String deptColumn = dataPermission.deptIdColumn();
                
                if (!deptAlias.isEmpty()) {
                    sqlString.append(deptAlias).append(".");
                }
                sqlString.append(deptColumn).append(" IN (");
                sqlString.append(dataScope.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
                sqlString.append(")");
            }
            
            if (dataPermission.filterUser()) {
                if (sqlString.length() > 0) {
                    sqlString.append(" OR ");
                }
                
                String userAlias = dataPermission.userAlias();
                String userColumn = dataPermission.userIdColumn();
                
                if (!userAlias.isEmpty()) {
                    sqlString.append(userAlias).append(".");
                }
                // 直接使用指定的用户列名，不做额外的表别名处理
                sqlString.append(userColumn);
                sqlString.append(" = ").append(userId);
            }
            
            // 将SQL片段存储到ThreadLocal中
            if (sqlString.length() > 0) {
                DataScopeContextHolder.setDataScope("(" + sqlString.toString() + ")");
            }
        }
    }

    /**
     * 清理数据权限
     */
    protected void clearDataScope() {
        DataScopeContextHolder.clear();
    }
}

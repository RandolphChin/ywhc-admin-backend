package com.ywhc.admin.common.aspect;

import com.ywhc.admin.common.annotation.DataPermission;
import com.ywhc.admin.common.context.DataScopeContextHolder;
import com.ywhc.admin.common.util.SecurityUtils;
import com.ywhc.admin.modules.system.dept.service.SysDeptService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
    @Around("@annotation(dataPermission)")
    public Object doAround(ProceedingJoinPoint point, DataPermission dataPermission) throws Throwable {
        // JDK 21: 使用 try-with-resources 的自定义资源管理
        try (var dataScopeManager = new DataScopeManager()) {
            handleDataScope(dataPermission);
            return point.proceed();
        }
    }

    /**
     * 数据权限范围管理器 - 实现 AutoCloseable 接口
     * JDK 21: 使用内部类实现资源管理
     */
    private final class DataScopeManager implements AutoCloseable {
        
        public DataScopeManager() {
            clearDataScope();
        }
        
        @Override
        public void close() {
            clearDataScope();
        }
    }

    /**
     * 处理数据权限
     */
    protected void handleDataScope(DataPermission dataPermission) {
        // 获取当前用户
        Long userId = SecurityUtils.getUserId();
        log.debug("DataPermission - 当前用户ID: {}", userId);
        
        if (userId == null) {
            log.warn("DataPermission - 无法获取当前用户ID，跳过数据权限处理");
            return;
        }

        // 获取用户数据权限范围
        Set<Long> dataScope = deptService.getDataScope(userId);
        log.debug("DataPermission - 用户 {} 的数据权限范围: {}", userId, dataScope);
        
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
                String userColumn = dataPermission.userIdColumn();
                
                // 只有当用户列名不为空时才添加用户权限条件
                if (userColumn != null && !userColumn.trim().isEmpty()) {
                    if (sqlString.length() > 0) {
                        sqlString.append(" OR ");
                    }
                    
                    String userAlias = dataPermission.userAlias();
                    
                    if (!userAlias.isEmpty()) {
                        sqlString.append(userAlias).append(".");
                    }
                    sqlString.append(userColumn);
                    sqlString.append(" = ").append(userId);
                }
            }
            
            // 将SQL片段存储到ThreadLocal中
            if (sqlString.length() > 0) {
                String finalSql = "(" + sqlString.toString() + ")";
                DataScopeContextHolder.setDataScope(finalSql);
                log.debug("DataPermission - 生成的数据权限SQL: {}", finalSql);
            }
        } else {
            log.debug("DataPermission - 用户 {} 没有数据权限范围或为空", userId);
        }
    }

    /**
     * 清理数据权限
     */
    protected void clearDataScope() {
        DataScopeContextHolder.clear();
    }
}

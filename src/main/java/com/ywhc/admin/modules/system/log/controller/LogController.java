package com.ywhc.admin.modules.system.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.modules.system.log.entity.SysLog;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.modules.system.log.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 日志管理控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "日志管理", description = "系统日志管理相关接口")
@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @LogAccess(value = "查询日志列表", module = "日志管理", operationType = OperationType.QUERY)
    @Operation(summary = "分页查询日志列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:log:list')")
    public Result<IPage<SysLog>> pageLogs(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "操作模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作描述") @RequestParam(required = false) String operationDesc,
            @Parameter(description = "操作状态") @RequestParam(required = false) Integer status) {
        IPage<SysLog> page = logService.pageLogs(current, size, module, operationDesc, status);
        return Result.success(page);
    }

    @LogAccess(value = "清空日志", module = "日志管理", operationType = OperationType.DELETE)
    @Operation(summary = "清空日志")
    @DeleteMapping("/clear")
    @PreAuthorize("hasAuthority('system:log:delete')")
    public Result<String> clearLogs() {
        logService.clearLogs();
        return Result.success("日志清空成功");
    }
}

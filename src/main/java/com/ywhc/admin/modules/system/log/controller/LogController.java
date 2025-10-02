package com.ywhc.admin.modules.system.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.annotation.DataPermission;
import com.ywhc.admin.modules.system.log.dto.LogQueryDTO;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.modules.system.log.service.LogService;
import com.ywhc.admin.modules.system.log.vo.LogVO;
import io.swagger.v3.oas.annotations.Operation;
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

    //@LogAccess(value = "查询日志列表", module = "日志管理", operationType = OperationType.QUERY)
    @Operation(summary = "分页查询日志列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:log:list')")
    // @DataPermission(deptIdColumn = "dept_id", userIdColumn = "user_id")
    @DataPermission(deptIdColumn = "dept_id")
    public Result<IPage<LogVO>> pageLogs(LogQueryDTO queryDTO) {
        IPage<LogVO> page = logService.pageLogs(queryDTO);
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

package com.ywhc.admin.modules.monitor.online.controller;

import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.modules.monitor.online.dto.OnlineUserQueryDTO;
import com.ywhc.admin.modules.monitor.online.service.OnlineUserService;
import com.ywhc.admin.modules.monitor.online.vo.OnlineUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户管理控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "在线用户管理", description = "在线用户监控相关接口")
@RestController
@RequestMapping("/monitor/online")
@RequiredArgsConstructor
public class OnlineUserController {

    private final OnlineUserService onlineUserService;

    @Operation(summary = "获取在线用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('monitor:online:list')")
    public Result<List<OnlineUserVO>> getOnlineUsers(OnlineUserQueryDTO queryDTO) {
        List<OnlineUserVO> onlineUsers = onlineUserService.getOnlineUsers(queryDTO);
        return Result.success(onlineUsers);
    }

    @Operation(summary = "获取在线用户总数")
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('monitor:online:list')")
    public Result<Long> getOnlineUserCount() {
        long count = onlineUserService.getOnlineUserCount();
        return Result.success(count);
    }

    @LogAccess(value = "强制用户下线", module = "在线用户管理", operationType = OperationType.DELETE)
    @Operation(summary = "强制用户下线")
    @DeleteMapping("/force-logout")
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    public Result<String> forceLogout(@Parameter(description = "访问Token") @RequestParam String token) {
        onlineUserService.forceLogout(token);
        return Result.success("用户已强制下线");
    }

    @LogAccess(value = "强制用户所有会话下线", module = "在线用户管理", operationType = OperationType.DELETE)
    @Operation(summary = "强制用户所有会话下线")
    @DeleteMapping("/force-logout-user/{userId}")
    @PreAuthorize("hasAuthority('monitor:online:forceLogout')")
    public Result<String> forceLogoutByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        onlineUserService.forceLogoutByUserId(userId);
        return Result.success("用户所有会话已强制下线");
    }

    @Operation(summary = "清理过期用户")
    @DeleteMapping("/clean-expired")
    @PreAuthorize("hasAuthority('monitor:online:clean')")
    public Result<String> cleanExpiredUsers() {
        onlineUserService.removeExpiredUsers();
        return Result.success("过期用户清理完成");
    }

    @Operation(summary = "检查Token是否在黑名单")
    @GetMapping("/check-blacklist")
    @PreAuthorize("hasAuthority('monitor:online:list')")
    public Result<Boolean> checkTokenBlacklist(@Parameter(description = "访问Token") @RequestParam String token) {
        boolean isBlacklisted = onlineUserService.isTokenBlacklisted(token);
        return Result.success(isBlacklisted);
    }
}

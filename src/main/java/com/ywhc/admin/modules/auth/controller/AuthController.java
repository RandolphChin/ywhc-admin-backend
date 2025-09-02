package com.ywhc.admin.modules.auth.controller;

import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.modules.auth.dto.LoginDTO;
import com.ywhc.admin.modules.auth.service.AuthService;
import com.ywhc.admin.modules.auth.vo.LoginVO;
import com.ywhc.admin.modules.auth.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "认证管理", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(
        @Valid @RequestBody LoginDTO loginDTO,
        HttpServletRequest request
    ) {
        String clientIp = getClientIp(request);
        LoginVO loginVO = authService.login(loginDTO, clientIp, request);
        return Result.success("登录成功", loginVO);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return Result.success("登出成功");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user-info")
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO userInfo = authService.getCurrentUserInfo();
        return Result.success(userInfo);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@RequestParam String refreshToken) {
        LoginVO loginVO = authService.refreshToken(refreshToken);
        return Result.success("Token刷新成功", loginVO);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xfor = request.getHeader("X-Forwarded-For");

        if (
            xfor != null && !xfor.isEmpty() && !"unknown".equalsIgnoreCase(xfor)
        ) {
            int index = xfor.indexOf(",");
            if (index != -1) {
                return xfor.substring(0, index);
            } else {
                return xfor;
            }
        }

        if (xip != null && !xip.isEmpty() && !"unknown".equalsIgnoreCase(xip)) {
            return xip;
        }

        if (
            xfor != null && !xfor.isEmpty() && !"unknown".equalsIgnoreCase(xfor)
        ) {
            return xfor;
        }

        return request.getRemoteAddr();
    }
}

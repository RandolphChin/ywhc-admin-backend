package com.ywhc.admin.common.util;

import com.ywhc.admin.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 安全工具类
 * 用于获取当前用户信息
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final JwtUtils jwtUtils;

    /**
     * 获取当前用户名
     * 需要根据你的认证方式进行调整
     */
    public String getCurrentUsername() {
        // 方式1: 从Spring Security获取
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         if (authentication != null && authentication.isAuthenticated()) {
//             return authentication.getName();
//         }

        // 方式2: 从JWT token获取
        // 这里需要根据你的JWT实现来获取用户信息

        // 方式3: 从请求头获取
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null) {
                // 解析token获取用户名
                // 这里需要根据你的token解析逻辑来实现
                token = token.replace(jwtUtils.getTokenPrefix(), "");
                String username = jwtUtils.getUsernameFromToken(token);
                return username;
            }
        }

        return "anonymous";
    }

    /**
     * 获取当前用户ID
     */
    public Long getCurrentUserId() {
        // 方式3: 从请求头获取
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");
            if (token != null) {
                // 解析token获取用户名
                // 这里需要根据你的token解析逻辑来实现
                token = token.replace(jwtUtils.getTokenPrefix(), "");
                Long userId = jwtUtils.getUserIdFromToken(token);
                return userId;
            }
        }

        return null;
    }
}

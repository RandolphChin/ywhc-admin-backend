package com.ywhc.admin.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.result.ResultCode;
import com.ywhc.admin.common.security.service.UserDetailsServiceImpl;
import com.ywhc.admin.common.utils.JwtUtils;
import com.ywhc.admin.modules.monitor.online.entity.OnlineUser;
import com.ywhc.admin.modules.monitor.online.service.OnlineUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT认证过滤器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final OnlineUserService onlineUserService;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // 获取请求路径
        String requestPath = request.getServletPath();
        
        // 刷新token接口不需要进行Redis token存在性校验，因为刷新时原token可能已失效
        boolean isRefreshTokenRequest = "/auth/refresh".equals(requestPath);
        
        // 获取Token
        String token = getTokenFromRequest(request);

        if (
            StringUtils.hasText(token) &&
            SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            try {
                // 检查Token是否在黑名单中
                if (onlineUserService.isTokenBlacklisted(token)) {
                    log.debug("Token已在黑名单中: {}", maskToken(token));
                    filterChain.doFilter(request, response);
                    return;
                }

                // 验证Token格式
                if (jwtUtils.isValidToken(token)) {
                    // 从Token中获取用户名
                    String username = jwtUtils.getUsernameFromToken(token);

                    // 新增：检查token是否在Redis中存在（刷新token接口跳过此检查）
                    if (!isRefreshTokenRequest) {
                        OnlineUser onlineUser = onlineUserService.getOnlineUserByToken(token);
                        if (onlineUser == null) {
                            log.warn("Token在Redis中不存在，可能已被清理 - username: {}, token: {}, ip: {}",
                                username, maskToken(token), request.getRemoteAddr());
                            
                            // 使用统一Result格式返回401响应
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            
                            Result<Object> result = Result.error(ResultCode.UNAUTHORIZED);
                            
                            // 使用Spring管理的ObjectMapper
                            
                            String json = objectMapper.writeValueAsString(result);
                            response.getWriter().write(json);
                            return;
                        }

                        // 记录Redis token验证通过的日志
                        log.debug("Redis token验证通过 - username: {}, 最后访问时间: {}",
                            username, onlineUser.getLastAccessTime());
                    } else {
                        log.debug("刷新token请求，跳过Redis存在性校验 - username: {}, token: {}",
                            username, maskToken(token));
                    }

                    // 加载用户详情
                    UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                    // 验证Token有效性
                    if (jwtUtils.validateToken(token, username)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                            );

                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(
                                request
                            )
                        );

                        // 设置到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(
                            authentication
                        );

                        // 更新用户最后活动时间（刷新token请求跳过此操作）
                        if (!isRefreshTokenRequest) {
                            onlineUserService.updateLastAccessTime(token);
                        }

                        log.debug("用户 {} 认证成功", username);
                    }
                }
            } catch (Exception e) {
                log.error("JWT认证失败: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        String tokenPrefix = jwtUtils.getTokenPrefix();
        if (
            StringUtils.hasText(bearerToken) &&
            bearerToken.startsWith(tokenPrefix)
        ) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }

    /**
     * Token脱敏处理
     */
    private String maskToken(String token) {
        if (!StringUtils.hasText(token) || token.length() <= 10) {
            return token;
        }
        return (
            token.substring(0, 6) + "****" + token.substring(token.length() - 4)
        );
    }
}

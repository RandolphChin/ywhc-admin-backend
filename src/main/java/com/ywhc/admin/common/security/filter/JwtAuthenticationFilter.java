package com.ywhc.admin.common.security.filter;

import com.ywhc.admin.common.security.service.UserDetailsServiceImpl;
import com.ywhc.admin.common.utils.JwtUtils;
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

    private static final String TOKEN_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
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

                        // 更新用户最后活动时间
                        onlineUserService.updateLastAccessTime(token);

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

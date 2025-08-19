package com.ywhc.admin.common.security.filter;

import com.ywhc.admin.common.security.service.UserDetailsServiceImpl;
import com.ywhc.admin.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // 获取Token
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 验证Token格式
                if (jwtUtils.isValidToken(token)) {
                    // 从Token中获取用户名
                    String username = jwtUtils.getUsernameFromToken(token);

                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 验证Token有效性
                    if (jwtUtils.validateToken(token, username)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);

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
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}

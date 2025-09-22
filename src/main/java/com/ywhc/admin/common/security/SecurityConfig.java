package com.ywhc.admin.common.security;

import com.ywhc.admin.common.security.filter.JwtAuthenticationFilter;
import com.ywhc.admin.common.security.handler.AuthenticationEntryPointImpl;
import com.ywhc.admin.common.security.handler.AccessDeniedHandlerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security配置类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF
            .csrf(AbstractHttpConfigurer::disable)

            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 配置会话管理
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置异常处理
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )

            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers(
                    "/auth/**",
                    "/captcha/**",  // 滑块验证码相关接口
                    "/doc.html",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/v3/api-docs/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()

                // 其他请求需要认证
                .anyRequest().authenticated()
            )

            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS配置 - 统一在Security中管理
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));

        // 允许的方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));

        // 允许的头部
        configuration.setAllowedHeaders(Collections.singletonList("*"));

        // 允许携带凭证
        configuration.setAllowCredentials(true);

        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        // 允许的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Content-Length",
            "Access-Control-Allow-Origin", 
            "Access-Control-Allow-Headers",
            "Cache-Control",
            "Content-Language",
            "Content-Type",
            "Expires",
            "Last-Modified",
            "Pragma"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
package com.ywhc.admin.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.result.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 认证失败处理器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        log.error("认证失败: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Result<Object> result = Result.error(ResultCode.UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);

        response.getWriter().write(json);
    }
}

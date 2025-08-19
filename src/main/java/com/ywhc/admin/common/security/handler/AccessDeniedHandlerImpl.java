package com.ywhc.admin.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.result.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 权限不足处理器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("权限不足: {}", accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        Result<Object> result = Result.error(ResultCode.FORBIDDEN);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);

        response.getWriter().write(json);
    }
}

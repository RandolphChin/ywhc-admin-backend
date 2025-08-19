package com.ywhc.admin.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywhc.admin.common.utils.JwtUtils;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.modules.system.log.entity.SysLog;
import com.ywhc.admin.modules.system.log.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 日志切面
 * 用于拦截带有@LogAccess注解的方法，自动记录操作日志
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogService logService;
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    @Around("@annotation(com.ywhc.admin.common.annotation.LogAccess)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取方法签名和注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAccess logAccess = method.getAnnotation(LogAccess.class);

        // 创建日志对象
        SysLog sysLog = new SysLog();
        sysLog.setCreateTime(LocalDateTime.now());

        if (request != null) {
            sysLog.setRequestMethod(request.getMethod());
            sysLog.setRequestUrl(request.getRequestURI());
            sysLog.setIpAddress(getClientIpAddress(request));
            sysLog.setUserAgent(request.getHeader("User-Agent"));

            // 获取请求参数
            String params = getRequestParams(joinPoint, request);
            sysLog.setRequestParams(params);
        }

        // 设置注解信息
        if (logAccess != null) {
            sysLog.setOperationDesc(logAccess.value());
            sysLog.setModule(logAccess.module());
            sysLog.setOperationType(logAccess.operationType().getCode());
        }

        // 获取当前用户信息（需要根据你的认证方式调整）
        try {
            // 这里需要根据你的用户认证方式来获取用户信息
            // 例如从SecurityContext或JWT token中获取
            String username = getCurrentUsername();
            sysLog.setUsername(username);
        } catch (Exception e) {
            log.warn("获取当前用户信息失败: {}", e.getMessage());
        }

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();

            // 记录成功信息
            sysLog.setStatus(1); // 成功
            sysLog.setExecutionTime(System.currentTimeMillis() - startTime);

            // 记录响应结果（可选，避免过大的响应数据）
            if (result != null) {
                try {
                    String resultStr = objectMapper.writeValueAsString(result);
                    if (resultStr.length() > 2000) {
                        resultStr = resultStr.substring(0, 2000) + "...";
                    }
                    sysLog.setResponseResult(resultStr);
                } catch (Exception e) {
                    sysLog.setResponseResult("响应结果序列化失败");
                }
            }

        } catch (Exception e) {
            // 记录异常信息
            sysLog.setStatus(0); // 失败
            sysLog.setExecutionTime(System.currentTimeMillis() - startTime);
            sysLog.setErrorMsg(e.getMessage());

            throw e; // 重新抛出异常
        } finally {
            // 异步保存日志
            try {
                logService.saveLog(sysLog);
            } catch (Exception e) {
                log.error("保存操作日志失败: {}", e.getMessage());
            }
        }

        return result;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        try {
            // 获取方法参数
            Object[] args = joinPoint.getArgs();
            String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

            StringBuilder params = new StringBuilder();

            // 添加URL参数
            if (request.getQueryString() != null) {
                params.append("Query: ").append(request.getQueryString()).append("; ");
            }

            // 添加方法参数
            if (args != null && args.length > 0) {
                params.append("Args: ");
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null) {
                        String paramName = paramNames != null && i < paramNames.length ? paramNames[i] : "arg" + i;
                        params.append(paramName).append("=").append(args[i].toString()).append("; ");
                    }
                }
            }

            String result = params.toString();
            return result.length() > 1000 ? result.substring(0, 1000) + "..." : result;

        } catch (Exception e) {
            return "参数获取失败: " + e.getMessage();
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] ipHeaders = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };

        for (String header : ipHeaders) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 获取当前用户名
     * 需要根据你的认证方式进行调整
     */
    private String getCurrentUsername() {
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
}

package com.ywhc.admin.common.security.config;

import com.ywhc.admin.common.security.service.RSAKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * RSA密钥初始化器
 * 应用启动时自动初始化RSA密钥对
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RSAKeyInitializer implements ApplicationRunner {

    private final RSAKeyService rsaKeyService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化RSA密钥对...");
        try {
            rsaKeyService.initKeyPair();
            log.info("RSA密钥对初始化完成");
        } catch (Exception e) {
            log.error("RSA密钥对初始化失败", e);
            // 不抛出异常，避免影响应用启动
        }
    }
}
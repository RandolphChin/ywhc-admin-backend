package com.ywhc.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Configuration
@MapperScan(basePackages = "com.ywhc.admin.modules.**.mapper")
public class MyBatisConfig {
    
}

package com.ywhc.admin.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置类
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                Long userId = getCurrentUserId();
                
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
                this.strictInsertFill(metaObject, "createBy", Long.class, userId);
                this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                Long userId = getCurrentUserId();
                
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
                this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
            }

            /**
             * 获取当前用户ID
             */
            private Long getCurrentUserId() {
                try {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.getPrincipal() instanceof com.ywhc.admin.framework.security.service.SecurityUser securityUser) {
                        return securityUser.getUserId();
                    }
                } catch (Exception e) {
                    // 忽略异常，返回默认值
                }
                return 1L; // 默认系统用户ID
            }
        };
    }
}

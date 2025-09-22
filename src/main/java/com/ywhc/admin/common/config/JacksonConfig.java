package com.ywhc.admin.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson配置类
 * 解决前端Long类型精度丢失问题
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置Jackson ObjectMapper
     * 将Long类型序列化为字符串，避免前端精度丢失
     * 支持LocalDateTime的反序列化
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 注册JavaTimeModule以支持LocalDateTime等Java8时间类型
        objectMapper.registerModule(new JavaTimeModule());

        // 创建自定义模块
        SimpleModule simpleModule = new SimpleModule();

        // 将Long类型序列化为字符串
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 添加LocalDateTime反序列化器
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

        // 注册模块
        objectMapper.registerModule(simpleModule);

        return objectMapper;
    }

    /**
     * 自定义LocalDateTime反序列化器
     * 支持多种日期时间格式
     */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter[] FORMATTERS = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        };

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();

            if (!StringUtils.hasText(value)) {
                return null;
            }

            // 尝试使用不同的格式解析
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(value, formatter);
                } catch (Exception ignored) {
                    // 继续尝试下一个格式
                }
            }

            // 如果所有格式都失败，抛出异常
            throw new IOException("无法解析日期时间格式: " + value);
        }
    }
}

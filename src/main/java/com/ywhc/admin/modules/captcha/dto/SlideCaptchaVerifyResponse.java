package com.ywhc.admin.modules.captcha.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 滑块验证码验证响应DTO
 * 
 * @author YWHC Team
 * @since 2024-09-22
 */
@Data
@Builder
public class SlideCaptchaVerifyResponse {
    
    /**
     * 验证是否成功
     */
    private Boolean success;
    
    /**
     * 验证token（验证成功时返回）
     */
    private String token;
    
    /**
     * 错误信息（验证失败时返回）
     */
    private String message;
}

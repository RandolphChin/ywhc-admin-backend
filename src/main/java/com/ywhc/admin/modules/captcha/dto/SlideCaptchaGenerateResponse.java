package com.ywhc.admin.modules.captcha.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 滑块验证码生成响应DTO
 * 
 * @author YWHC Team
 * @since 2024-09-22
 */
@Data
@Builder
public class SlideCaptchaGenerateResponse {
    
    /**
     * 验证码ID
     */
    private String captchaId;
    
    /**
     * 背景图片Base64
     */
    private String backgroundImage;
    
    /**
     * 拼图块图片Base64
     */
    private String puzzleImage;
    
    /**
     * 拼图块X坐标
     */
    private Integer puzzleX;
    
    /**
     * 拼图块Y坐标
     */
    private Integer puzzleY;
}

package com.ywhc.admin.modules.captcha.service;

import com.ywhc.admin.modules.captcha.dto.SlideCaptchaGenerateResponse;
import com.ywhc.admin.modules.captcha.dto.SlideCaptchaVerifyRequest;
import com.ywhc.admin.modules.captcha.dto.SlideCaptchaVerifyResponse;

/**
 * 滑块验证码服务接口
 * 
 * @author YWHC Team
 * @since 2024-09-22
 */
public interface SlideCaptchaService {
    
    /**
     * 生成滑块验证码
     * 
     * @return 验证码生成响应
     */
    SlideCaptchaGenerateResponse generateCaptcha();
    
    /**
     * 验证滑块验证码
     * 
     * @param request 验证请求
     * @return 验证响应
     */
    SlideCaptchaVerifyResponse verifyCaptcha(SlideCaptchaVerifyRequest request);
    
    /**
     * 验证token是否有效
     * 
     * @param token 验证token
     * @return 是否有效
     */
    boolean validateToken(String token);
}

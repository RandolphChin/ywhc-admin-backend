package com.ywhc.admin.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录DTO
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码（可能是明文或加密后的密码）
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 是否为加密密码
     */
    private Boolean encrypted = false;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 验证码key
     */
    private String captchaKey;

    /**
     * 滑块验证码token
     */
    private String captchaToken;

    /**
     * 记住我
     */
    private Boolean rememberMe = false;
}

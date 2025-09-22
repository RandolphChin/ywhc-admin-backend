package com.ywhc.admin.modules.captcha.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 滑块验证码实体类
 * 
 * @author YWHC Team
 * @since 2024-09-22
 */
@Data
public class SlideCaptcha {
    
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
    
    /**
     * 拼图块宽度
     */
    private Integer puzzleWidth;
    
    /**
     * 拼图块高度
     */
    private Integer puzzleHeight;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 是否已验证
     */
    private Boolean verified;
    
    /**
     * 验证token
     */
    private String token;
}

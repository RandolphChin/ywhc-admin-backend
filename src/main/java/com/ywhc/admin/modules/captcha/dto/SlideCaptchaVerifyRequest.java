package com.ywhc.admin.modules.captcha.dto;

import lombok.Data;
import java.util.List;

/**
 * 滑块验证码验证请求DTO
 * 
 * @author YWHC Team
 * @since 2024-09-22
 */
@Data
public class SlideCaptchaVerifyRequest {
    
    /**
     * 验证码ID
     */
    private String captchaId;
    
    /**
     * 滑块X坐标
     */
    private Integer slideX;
    
    /**
     * 拖拽轨迹
     */
    private List<TrackPoint> track;
    
    /**
     * 轨迹点
     */
    @Data
    public static class TrackPoint {
        /**
         * X坐标
         */
        private Integer x;
        
        /**
         * Y坐标
         */
        private Integer y;
        
        /**
         * 时间戳（相对于开始拖拽的时间）
         */
        private Long time;
    }
}

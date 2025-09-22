package com.ywhc.admin.modules.captcha.controller;

import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.modules.captcha.dto.SlideCaptchaGenerateResponse;
import com.ywhc.admin.modules.captcha.dto.SlideCaptchaVerifyRequest;
import com.ywhc.admin.modules.captcha.dto.SlideCaptchaVerifyResponse;
import com.ywhc.admin.modules.captcha.service.SlideCaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 滑块验证码控制器
 *
 * @author YWHC Team
 * @since 2024-09-22
 */
@Slf4j
@RestController
@RequestMapping("/captcha/slide")
@RequiredArgsConstructor
@Tag(name = "滑块验证码", description = "滑块验证码相关接口")
public class SlideCaptchaController {

    private final SlideCaptchaService slideCaptchaService;

    /**
     * 生成滑块验证码
     */
    @GetMapping("/generate")
    @Operation(summary = "生成滑块验证码", description = "生成新的滑块验证码")
    public Result<SlideCaptchaGenerateResponse> generateCaptcha() {
        try {
            SlideCaptchaGenerateResponse response = slideCaptchaService.generateCaptcha();
            return Result.success(response);
        } catch (Exception e) {
            log.error("生成滑块验证码失败", e);
            return Result.error("生成验证码失败");
        }
    }

    /**
     * 验证滑块验证码
     */
    @PostMapping("/verify")
    @Operation(summary = "验证滑块验证码", description = "验证用户滑块操作是否正确")
    public Result<SlideCaptchaVerifyResponse> verifyCaptcha(@RequestBody SlideCaptchaVerifyRequest request) {
        try {
            SlideCaptchaVerifyResponse response = slideCaptchaService.verifyCaptcha(request);
            // 无论验证成功还是失败，都返回成功的HTTP响应，让前端根据response.success判断
            return Result.success(response);
        } catch (Exception e) {
            log.error("验证滑块验证码失败", e);
            return Result.error("验证失败");
        }
    }

    /**
     * 验证token有效性
     */
    @GetMapping("/validate/{token}")
    @Operation(summary = "验证token", description = "验证验证码token是否有效")
    public Result<Boolean> validateToken(@PathVariable String token) {
        try {
            boolean valid = slideCaptchaService.validateToken(token);
            return Result.success(valid);
        } catch (Exception e) {
            log.error("验证token失败", e);
            return Result.error("验证失败");
        }
    }
}

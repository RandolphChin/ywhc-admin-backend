package com.ywhc.admin.modules.auth.controller;

import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.result.ResultCode;
import com.ywhc.admin.common.security.service.RSAKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 加密相关控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "加密管理", description = "加密相关接口")
@RestController
@RequestMapping("/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final RSAKeyService rsaKeyService;

    @Operation(summary = "获取RSA公钥")
    @GetMapping("/public-key")
    public Result<Map<String, Object>> getPublicKey() {
        String publicKey = rsaKeyService.getPublicKeyBase64();

        Map<String, Object> data = new HashMap<>();
        data.put("publicKey", publicKey);
        data.put("timestamp", System.currentTimeMillis());

        return Result.success("获取公钥成功", data);
    }
/*
    @Operation(summary = "测试RSA加密解密")
    @PostMapping("/test-encrypt")
    public Result<Map<String, Object>> testEncrypt(@RequestBody Map<String, String> request) {
        String encryptedData = request.get("encryptedData");

        try {
            String decryptedData = rsaKeyService.decrypt(encryptedData);

            Map<String, Object> data = new HashMap<>();
            data.put("decryptedData", decryptedData);
            data.put("success", true);

            return Result.success("解密成功", data);
        } catch (Exception e) {
            Map<String, Object> data = new HashMap<>();
            data.put("error", e.getMessage());
            data.put("success", false);

            return Result.error(ResultCode.SUCCESS.getCode(), e.getMessage());
        }
    }
    */
}

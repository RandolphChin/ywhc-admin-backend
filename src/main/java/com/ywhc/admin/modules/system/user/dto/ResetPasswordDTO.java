package com.ywhc.admin.modules.system.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 重置密码DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "重置密码DTO")
public class ResetPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    @Schema(description = "是否加密传输")
    private Boolean encrypted = false;
}

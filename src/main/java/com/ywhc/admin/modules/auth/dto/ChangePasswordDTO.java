package com.ywhc.admin.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码DTO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@Schema(description = "修改密码DTO")
public class ChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "原密码")
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String newPassword;
}

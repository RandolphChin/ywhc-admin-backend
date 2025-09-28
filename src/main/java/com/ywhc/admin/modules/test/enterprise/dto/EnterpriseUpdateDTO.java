package com.ywhc.admin.modules.test.enterprise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 测试企业修改DTO
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
@Data
@Schema(name = "EnterpriseUpdateDTO", description = "测试企业修改DTO")
public class EnterpriseUpdateDTO {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "企业名称")
    @Size(max = 255, message = "企业名称长度不能超过255个字符")
    private String enterpriseName;

    @Schema(description = "企业地址")
    @Size(max = 255, message = "企业地址长度不能超过255个字符")
    private String enterpriseAddress;

    @Schema(description = "数据权限-当前用户所在部门")
    private Integer deptId;

    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    @Schema(description = "删除标志：0-正常，1-删除")
    private Integer deleted;

}

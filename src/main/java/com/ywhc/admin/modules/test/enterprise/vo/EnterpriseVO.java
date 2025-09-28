package com.ywhc.admin.modules.test.enterprise.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * 测试企业VO
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
@Data
@Schema(name = "EnterpriseVO", description = "测试企业VO")
public class EnterpriseVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "企业名称")
    private String enterpriseName;

    @Schema(description = "企业地址")
    private String enterpriseAddress;

    @Schema(description = "数据权限-当前用户所在部门")
    private Integer deptId;

    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    @Schema(description = "删除标志：0-正常，1-删除")
    private Integer deleted;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private String createByName;

    @Schema(description = "更新者")
    private String updateByName;

}

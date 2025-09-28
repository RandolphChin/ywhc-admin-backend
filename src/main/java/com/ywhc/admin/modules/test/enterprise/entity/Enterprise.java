package com.ywhc.admin.modules.test.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 测试企业实体类
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
@Data
@TableName("biz_enterprise")
@Schema(name = "Enterprise", description = "测试企业")
public class Enterprise implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "企业名称")
    @TableField("enterprise_name")
    private String enterpriseName;

    @Schema(description = "企业地址")
    @TableField("enterprise_address")
    private String enterpriseAddress;

    @Schema(description = "数据权限-当前用户所在部门")
    @TableField("dept_id")
    private Integer deptId;

    @Schema(description = "状态：0-禁用，1-正常")
    @TableField("status")
    private Integer status;

    @Schema(description = "删除标志：0-正常，1-删除")
    @TableField("deleted")
    private Integer deleted;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    @Schema(description = "更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;


}
package com.ywhc.admin.modules.test.enterprise.dto;

import com.ywhc.admin.common.dto.BaseQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ywhc.admin.common.annotation.QueryField;
import com.ywhc.admin.common.annotation.QueryType;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;
import java.time.LocalDateTime;

/**
 * 测试企业查询DTO
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "EnterpriseQueryDTO", description = "测试企业查询DTO")
public class EnterpriseQueryDTO extends BaseQueryDTO {

    @Schema(description = "企业名称")
    @QueryField(column = "enterprise_name", type = QueryType.LIKE)
    private String enterpriseName;

    @Schema(description = "企业地址")
    @QueryField(column = "enterprise_address", type = QueryType.LIKE)
    private String enterpriseAddress;

    @Schema(description = "数据权限-当前用户所在部门")
    @QueryField(column = "dept_id", type = QueryType.LIKE)
    private Integer deptId;

    @Schema(description = "状态：0-禁用，1-正常")
    @QueryField(column = "status", type = QueryType.LIKE)
    private Integer status;

    @Schema(description = "删除标志：0-正常，1-删除")
    @QueryField(column = "deleted", type = QueryType.LIKE)
    private Integer deleted;

    @Schema(description = "创建者")
    @QueryField(column = "create_by", type = QueryType.LIKE)
    private Long createBy;

    @Schema(description = "更新者")
    @QueryField(column = "update_by", type = QueryType.LIKE)
    private Long updateBy;

    
    /**
     * 创建时间范围查询
     */
    @Schema(description = "创建时间范围")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") // 用于URL参数
    @QueryField(column = "create_time", type = QueryType.DATE_RANGE)
    private List<LocalDateTime> createTimeBetween;

    /**
     * 更新时间范围查询
     */
    @Schema(description = "更新时间范围")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") // 用于URL参数
    @QueryField(column = "update_time", type = QueryType.DATE_RANGE)
    private List<LocalDateTime> updateTimeBetween;

    
}

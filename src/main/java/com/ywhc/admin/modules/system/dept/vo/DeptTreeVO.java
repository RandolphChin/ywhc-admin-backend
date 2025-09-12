package com.ywhc.admin.modules.system.dept.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门树形结构VO
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class DeptTreeVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门类型：1-公司，2-部门，3-小组
     */
    private Integer deptType;

    /**
     * 部门类型名称
     */
    private String deptTypeName;

    /**
     * 部门层级路径
     */
    private String ancestors;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 子部门列表
     */
    private List<DeptTreeVO> children;

    /**
     * 是否有子节点
     */
    private Boolean hasChildren;
}

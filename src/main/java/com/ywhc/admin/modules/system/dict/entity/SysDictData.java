package com.ywhc.admin.modules.system.dict.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典数据实体类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict_data")
public class SysDictData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 字典排序
     */
    @TableField("dict_sort")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @TableField("dict_label")
    private String dictLabel;

    /**
     * 字典键值
     */
    @TableField("dict_value")
    private String dictValue;

    /**
     * 字典类型
     */
    @TableField("dict_type")
    private String dictType;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 删除标志：0-正常，1-删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

}

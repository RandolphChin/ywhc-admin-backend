package com.ywhc.admin.modules.system.dept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.system.dept.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统部门Mapper接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门列表（包含子部门数量）
     * 
     * @param deptName 部门名称
     * @param status 状态
     * @return 部门列表
     */
    List<SysDept> selectDeptList(@Param("deptName") String deptName, @Param("status") Integer status);

    /**
     * 根据角色ID查询部门树信息
     * 
     * @param roleId 角色ID
     * @return 部门列表
     */
    List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据部门ID查询所有子部门
     * 
     * @param deptId 部门ID
     * @return 子部门ID列表
     */
    List<Long> selectChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 根据部门ID查询所有父级部门
     * 
     * @param deptId 部门ID
     * @return 父级部门ID列表
     */
    List<Long> selectAncestorsDeptById(@Param("deptId") Long deptId);

    /**
     * 修改子元素关系
     * 
     * @param depts 部门数据
     */
    void updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 检查部门名称是否唯一
     * 
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @param deptId 部门ID
     * @return 结果
     */
    int checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId, @Param("deptId") Long deptId);

    /**
     * 检查部门编码是否唯一
     * 
     * @param deptCode 部门编码
     * @param deptId 部门ID
     * @return 结果
     */
    int checkDeptCodeUnique(@Param("deptCode") String deptCode, @Param("deptId") Long deptId);
}

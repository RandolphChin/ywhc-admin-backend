package com.ywhc.admin.modules.system.dept.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.dept.dto.DeptQueryDTO;
import com.ywhc.admin.modules.system.dept.dto.DeptSaveDTO;
import com.ywhc.admin.modules.system.dept.entity.SysDept;
import com.ywhc.admin.modules.system.dept.vo.DeptTreeVO;
import java.util.List;
import java.util.Set;

/**
 * 系统部门服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门树形列表
     * 
     * @param queryDTO 查询条件
     * @return 部门树形列表
     */
    List<DeptTreeVO> getDeptTree(DeptQueryDTO queryDTO);

    /**
     * 根据ID查询部门信息
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    DeptTreeVO getDeptById(Long deptId);

    /**
     * 新增部门
     * 
     * @param saveDTO 部门信息
     * @return 结果
     */
    boolean saveDept(DeptSaveDTO saveDTO);

    /**
     * 修改部门
     * 
     * @param saveDTO 部门信息
     * @return 结果
     */
    boolean updateDept(DeptSaveDTO saveDTO);

    /**
     * 删除部门
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    boolean deleteDept(Long deptId);

    /**
     * 批量删除部门
     * 
     * @param deptIds 部门ID数组
     * @return 结果
     */
    boolean deleteDeptByIds(Long[] deptIds);

    /**
     * 校验部门名称是否唯一
     * 
     * @param saveDTO 部门信息
     * @return 结果
     */
    boolean checkDeptNameUnique(DeptSaveDTO saveDTO);

    /**
     * 校验部门编码是否唯一
     * 
     * @param saveDTO 部门信息
     * @return 结果
     */
    boolean checkDeptCodeUnique(DeptSaveDTO saveDTO);

    /**
     * 获取用户数据权限范围
     * @param userId 用户ID
     * @return 部门ID集合
     */
    Set<Long> getDataScope(Long userId);

    /**
     * 根据角色ID查询部门树信息
     * 
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    List<Long> getDeptIdsByRoleId(Long roleId);

    /**
     * 获取部门下拉树选择项
     * 
     * @return 部门树选择项
     */
    List<DeptTreeVO> getDeptTreeSelect();

    /**
     * 根据部门ID查询所有子部门（包含自身）
     * 
     * @param deptId 部门ID
     * @return 部门ID列表
     */
    List<Long> getChildrenDeptIds(Long deptId);

    /**
     * 根据部门ID查询所有父级部门（包含自身）
     * 
     * @param deptId 部门ID
     * @return 部门ID列表
     */
    List<Long> getAncestorsDeptIds(Long deptId);
}

package com.ywhc.admin.modules.system.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.role.dto.RoleCreateDTO;
import com.ywhc.admin.modules.system.role.dto.RoleUpdateDTO;
import com.ywhc.admin.modules.system.role.entity.SysRole;
import com.ywhc.admin.modules.system.role.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface RoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     */
    IPage<RoleVO> pageRoles(Long current, Long size, String roleName, String roleKey, Integer status);

    /**
     * 获取所有角色列表
     */
    List<RoleVO> getAllRoles();

    /**
     * 根据用户ID获取角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 创建角色
     */
    Long createRole(RoleCreateDTO createDTO);

    /**
     * 更新角色
     */
    void updateRole(RoleUpdateDTO updateDTO);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 批量删除角色
     */
    void deleteRoles(List<Long> roleIds);

    /**
     * 修改角色状态
     */
    void updateStatus(Long roleId, Integer status);

    /**
     * 分配角色菜单权限
     */
    void assignMenus(Long roleId, Long[] menuIds);

    /**
     * 获取角色菜单权限
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 检查角色标识是否存在
     */
    boolean existsByRoleKey(String roleKey);

    /**
     * 检查角色名称是否存在
     */
    boolean existsByRoleName(String roleName);
}

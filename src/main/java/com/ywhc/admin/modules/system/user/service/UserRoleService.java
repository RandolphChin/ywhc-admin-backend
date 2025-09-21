package com.ywhc.admin.modules.system.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.user.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色关联服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface UserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户ID查询角色关联
     */
    List<SysUserRole> getByUserId(Long userId);

    /**
     * 根据角色ID查询用户关联
     */
    List<SysUserRole> getByRoleId(Long roleId);

    /**
     * 根据用户ID删除角色关联
     */
    void deleteByUserId(Long userId);

    /**
     * 根据角色ID删除用户关联
     */
    void deleteByRoleId(Long roleId);

    /**
     * 批量保存用户角色关联
     */
    void saveBatch(Long userId, List<Long> roleIds);

    /**
     * 检查用户是否拥有指定角色
     */
    boolean hasRole(Long userId, Long roleId);

    /**
     * 获取用户的所有角色ID
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 获取角色下的所有用户ID
     */
    List<Long> getUserIdsByRoleId(Long roleId);

    /**
     * 删除用户角色关联并重新分配
     */
    void reassignUserRoles(Long userId, List<Long> roleIds);
}

package com.ywhc.admin.modules.system.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.role.entity.SysRoleMenu;

import java.util.List;

/**
 * 角色菜单权限关联服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 根据菜单ID查询角色ID列表
     */
    List<Long> getRoleIdsByMenuId(Long menuId);

    /**
     * 为角色分配菜单权限
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 删除角色的所有菜单权限
     */
    void removeMenusByRoleId(Long roleId);

    /**
     * 删除菜单的所有角色关联
     */
    void removeRolesByMenuId(Long menuId);

    /**
     * 批量删除角色的菜单权限
     */
    void removeMenusByRoleIds(List<Long> roleIds);

    /**
     * 批量删除菜单的角色关联
     */
    void removeRolesByMenuIds(List<Long> menuIds);

    /**
     * 检查角色是否拥有指定菜单权限
     */
    boolean hasMenuPermission(Long roleId, Long menuId);

    /**
     * 批量保存角色菜单关联
     */
    void saveBatch(List<SysRoleMenu> roleMenuList);

    /**
     * 更新角色的菜单权限（先删除后新增）
     */
    void updateRoleMenus(Long roleId, List<Long> menuIds);
}

package com.ywhc.admin.modules.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.system.role.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单权限关联Mapper接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID查询菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID查询角色ID列表
     */
    List<Long> selectRoleIdsByMenuId(@Param("menuId") Long menuId);

    /**
     * 根据角色ID删除角色菜单关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单ID删除角色菜单关联
     */
    int deleteByMenuId(@Param("menuId") Long menuId);

    /**
     * 批量插入角色菜单关联
     */
    int batchInsert(@Param("list") List<SysRoleMenu> list);

    /**
     * 检查角色菜单关联是否存在
     */
    boolean existsByRoleIdAndMenuId(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    /**
     * 根据角色ID列表删除角色菜单关联
     */
    int deleteByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据菜单ID列表删除角色菜单关联
     */
    int deleteByMenuIds(@Param("menuIds") List<Long> menuIds);
}

package com.ywhc.admin.modules.system.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.system.role.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Mapper
public interface RoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色标识查询角色
     */
    SysRole selectByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 检查角色标识是否存在
     */
    boolean existsByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 检查角色名称是否存在
     */
    boolean existsByRoleName(@Param("roleName") String roleName);

    /**
     * 检查角色下是否有用户
     */
    boolean hasUsers(@Param("roleId") Long roleId);

    /**
     * 获取角色的菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}

package com.ywhc.admin.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.system.user.entity.SysUserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID查询角色关联
     */
    List<SysUserRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户关联
     */
    List<SysUserRole> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID删除角色关联
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除用户关联
     */
    @Delete("DELETE FROM sys_user_role WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色
     */
    int countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 获取用户的所有角色ID
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 获取角色下的所有用户ID
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关联
     */
    int insertBatch(@Param("list") List<SysUserRole> userRoleList);
}

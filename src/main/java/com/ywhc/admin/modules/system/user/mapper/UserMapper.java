package com.ywhc.admin.modules.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.system.role.vo.RoleVO;
import com.ywhc.admin.modules.system.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 用户Mapper接口
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询用户权限
     */
    Set<String> selectUserPermissions(@Param("userId") Long userId);

    /**
     * 根据用户ID查询用户角色
     */
    Set<String> selectUserRoles(@Param("userId") Long userId);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByMobile(@Param("mobile") String mobile);

    List<RoleVO> selectRoleInfoByUserId(@Param("userId") Long userId);
}

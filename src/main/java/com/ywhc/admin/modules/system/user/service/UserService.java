package com.ywhc.admin.modules.system.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.user.dto.ResetPasswordDTO;
import com.ywhc.admin.modules.system.user.dto.UserCreateDTO;
import com.ywhc.admin.modules.system.user.dto.UserQueryDTO;
import com.ywhc.admin.modules.system.user.dto.UserUpdateDTO;
import com.ywhc.admin.modules.system.user.entity.SysUser;
import com.ywhc.admin.modules.system.user.vo.UserVO;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface UserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    IPage<UserVO> pageUsers(UserQueryDTO queryDTO);

    /**
     * 根据用户名获取用户
     */
    SysUser getByUsername(String username);

    /**
     * 获取用户权限
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色
     */
    Set<String> getUserRoles(Long userId);

    /**
     * 创建用户
     */
    Long createUser(UserCreateDTO createDTO);

    /**
     * 更新用户
     */
    void updateUser(UserUpdateDTO updateDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    void deleteUsers(List<Long> userIds);

    /**
     * 重置用户密码
     */
    void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO);

    /**
     * 更新用户密码
     */
    void updatePassword(Long userId, String encodedPassword);

    /**
     * 修改用户状态
     */
    void updateStatus(Long userId, Integer status);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, Long[] roleIds);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByMobile(String mobile);

    /**
     * 更新用户登录信息
     */
    void updateLoginInfo(Long userId, String loginIp);
}

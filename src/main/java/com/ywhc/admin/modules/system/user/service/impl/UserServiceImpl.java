package com.ywhc.admin.modules.system.user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.common.result.ResultCode;
import com.ywhc.admin.common.util.PageConverter;
import com.ywhc.admin.common.util.QueryProcessor;
import com.ywhc.admin.modules.system.role.vo.RoleVO;
import com.ywhc.admin.modules.system.user.dto.ResetPasswordDTO;
import com.ywhc.admin.modules.system.user.dto.UserCreateDTO;
import com.ywhc.admin.modules.system.user.dto.UserQueryDTO;
import com.ywhc.admin.modules.system.user.dto.UserUpdateDTO;
import com.ywhc.admin.modules.system.user.entity.SysUser;
import com.ywhc.admin.modules.system.user.mapper.UserMapper;
import com.ywhc.admin.modules.system.user.service.UserRoleService;
import com.ywhc.admin.modules.system.user.service.UserService;
import com.ywhc.admin.modules.system.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.ywhc.admin.common.security.service.RSAKeyService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final RSAKeyService rsaKeyService;

    @Override
    public IPage<UserVO> pageUsers(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<SysUser> userPage = this.page(page, QueryProcessor.createQueryWrapper(queryDTO));

        // 使用 PageConverter 转换为VO
        return PageConverter.convert(userPage, this::convertToVO);
    }

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        return userMapper.selectUserPermissions(userId);
    }

    @Override
    public Set<String> getUserRoles(Long userId) {
        return userMapper.selectUserRoles(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateDTO createDTO) {
        // 检查用户名是否存在
        if (existsByUsername(createDTO.getUsername())) {
            throw new RuntimeException(ResultCode.USERNAME_EXISTS.getMessage());
        }

        // 检查邮箱是否存在
        if (StringUtils.hasText(createDTO.getEmail()) && existsByEmail(createDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        // 检查手机号是否存在
        if (StringUtils.hasText(createDTO.getMobile()) && existsByMobile(createDTO.getMobile())) {
            throw new RuntimeException("手机号已存在");
        }

        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(createDTO, user);
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);

        this.save(user);

        // 分配角色
        if (createDTO.getRoleIds() != null && createDTO.getRoleIds().length > 0) {
            assignRoles(user.getId(), createDTO.getRoleIds());
        }

        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateDTO updateDTO) {
        SysUser existUser = this.getById(updateDTO.getId());
        if (existUser == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        // 检查邮箱是否存在
        if (StringUtils.hasText(updateDTO.getEmail()) && !updateDTO.getEmail().equals(existUser.getEmail())) {
            if (existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("邮箱已存在");
            }
        }

        // 检查手机号是否存在
        if (StringUtils.hasText(updateDTO.getMobile()) && !updateDTO.getMobile().equals(existUser.getMobile())) {
            if (existsByMobile(updateDTO.getMobile())) {
                throw new RuntimeException("手机号已存在");
            }
        }

        // 更新用户信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(updateDTO, user);
        this.updateById(user);

        // 更新角色
        if (updateDTO.getRoleIds() != null) {
            assignRoles(updateDTO.getId(), updateDTO.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        if (userId.equals(1L)) {
            throw new RuntimeException("不能删除超级管理员");
        }
        this.removeById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(List<Long> userIds) {
        if (userIds.contains(1L)) {
            throw new RuntimeException("不能删除超级管理员");
        }
        this.removeByIds(userIds);
    }

    @Override
    public void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO) {
        // 处理密码解密
        String actualNewPassword = resetPasswordDTO.getNewPassword();
        
        if (Boolean.TRUE.equals(resetPasswordDTO.getEncrypted())) {
            try {
                // 解密新密码
                String decryptedNewData = rsaKeyService.decrypt(resetPasswordDTO.getNewPassword());
                String[] newParts = decryptedNewData.split("\\|");
                if (newParts.length != 2) {
                    throw new RuntimeException("新密码格式错误");
                }
                actualNewPassword = newParts[0];
                long newTimestamp = Long.parseLong(newParts[1]);
                
                // 验证时间戳（防重放攻击，允许5分钟内的请求）
                if (!rsaKeyService.isValidTimestamp(newTimestamp, 300)) {
                    throw new RuntimeException("请求已过期，请重新操作");
                }
                
                log.debug("密码解密成功，重置用户ID: {}", userId);
            } catch (NumberFormatException e) {
                throw new RuntimeException("密码格式错误", e);
            } catch (Exception e) {
                log.error("密码解密失败: {}", e.getMessage());
                throw new RuntimeException("密码解密失败，请刷新页面重试", e);
            }
        }
        
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(actualNewPassword));
        this.updateById(user);
        
        log.info("用户ID {} 密码重置成功", userId);
    }

    @Override
    public void updatePassword(Long userId, String encodedPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(encodedPassword);
        this.updateById(user);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        if (userId.equals(1L)) {
            throw new RuntimeException("不能禁用超级管理员");
        }

        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, Long[] roleIds) {

        if (roleIds != null && roleIds.length > 0) {
            userRoleService.reassignUserRoles(userId,Arrays.asList(roleIds));
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return userMapper.existsByMobile(mobile);
    }

    @Override
    public void updateLoginInfo(Long userId, String loginIp) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(loginIp);
        this.updateById(user);
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 设置性别描述
        vo.setGenderDesc(getGenderDesc(user.getGender()));

        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(user.getStatus()));

        // 设置角色信息
        vo.setRoles(getUserRoleInfo(user.getId()));

        return vo;
    }

    /**
     * 获取性别描述
     */
    private String getGenderDesc(Integer gender) {
        return switch (gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        return status == 1 ? "正常" : "禁用";
    }
    private List<UserVO.RoleInfo> getUserRoleInfo(Long userId){
        List<RoleVO> voList = userMapper.selectRoleInfoByUserId(userId);
        List<UserVO.RoleInfo> roleInfos = new ArrayList<>();

        // 将 RoleVO 中的属性复制到 UserVO.RoleInfo
        for (RoleVO roleVO : voList) {
            UserVO.RoleInfo roleInfo = new UserVO.RoleInfo();
            roleInfo.setRoleId(roleVO.getId());
            roleInfo.setRoleName(roleVO.getRoleName());
            roleInfo.setRoleKey(roleVO.getRoleKey());
            roleInfos.add(roleInfo);
        }

        return roleInfos;
    }
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String finalPassword = bCryptPasswordEncoder.encode("admin123");
        System.out.println(finalPassword);
    }
}

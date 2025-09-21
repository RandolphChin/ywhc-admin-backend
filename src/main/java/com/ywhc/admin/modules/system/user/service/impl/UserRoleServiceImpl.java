package com.ywhc.admin.modules.system.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.modules.system.user.entity.SysUserRole;
import com.ywhc.admin.modules.system.user.mapper.UserRoleMapper;
import com.ywhc.admin.modules.system.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, SysUserRole> implements UserRoleService {

    private final UserRoleMapper userRoleMapper;

    @Override
    public List<SysUserRole> getByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return userRoleMapper.selectByUserId(userId);
    }

    @Override
    public List<SysUserRole> getByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return userRoleMapper.selectByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        userRoleMapper.deleteByUserId(userId);
        log.info("删除用户ID为{}的角色关联", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        userRoleMapper.deleteByRoleId(roleId);
        log.info("删除角色ID为{}的用户关联", roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(Long userId, List<Long> roleIds) {
        if (userId == null || CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        // 先删除原有关联
        deleteByUserId(userId);

        // 批量插入新关联
        List<SysUserRole> userRoleList = roleIds.stream()
                .map(roleId -> {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());

        if (!userRoleList.isEmpty()) {
            saveBatch(userRoleList);
            log.info("为用户ID{}分配了{}个角色", userId, roleIds.size());
        }
    }

    @Override
    public boolean hasRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return false;
        }
        return userRoleMapper.countByUserIdAndRoleId(userId, roleId) > 0;
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public List<Long> getUserIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return new ArrayList<>();
        }
        return userRoleMapper.selectUserIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reassignUserRoles(Long userId, List<Long> roleIds) {
        if (userId == null) {
            return;
        }

        // 删除原有角色关联
        deleteByUserId(userId);

        // 如果有新角色，则重新分配
        if (!CollectionUtils.isEmpty(roleIds)) {
            saveBatch(userId, roleIds);
        }

        log.info("重新分配用户ID{}的角色，共{}个角色", userId, 
                CollectionUtils.isEmpty(roleIds) ? 0 : roleIds.size());
    }
}

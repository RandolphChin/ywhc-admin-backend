package com.ywhc.admin.modules.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.common.result.ResultCode;
import com.ywhc.admin.modules.system.role.dto.RoleCreateDTO;
import com.ywhc.admin.modules.system.role.dto.RoleUpdateDTO;
import com.ywhc.admin.modules.system.role.entity.SysRole;
import com.ywhc.admin.modules.system.role.entity.SysRoleMenu;
import com.ywhc.admin.modules.system.role.mapper.RoleMapper;
import com.ywhc.admin.modules.system.role.service.RoleService;
import com.ywhc.admin.modules.system.role.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public IPage<RoleVO> pageRoles(Long current, Long size, String roleName, String roleKey, Integer status) {
        Page<SysRole> page = new Page<>(current, size);
        
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
               .like(StringUtils.hasText(roleKey), SysRole::getRoleKey, roleKey)
               .eq(status != null, SysRole::getStatus, status)
               .orderByAsc(SysRole::getSortOrder)
               .orderByDesc(SysRole::getCreateTime);

        IPage<SysRole> rolePage = this.page(page, wrapper);
        
        // 转换为VO
        List<RoleVO> roleVOList = rolePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<RoleVO> voPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        voPage.setRecords(roleVOList);
        
        return voPage;
    }

    @Override
    public List<RoleVO> getAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1)
               .orderByAsc(SysRole::getSortOrder);
        
        List<SysRole> roles = this.list(wrapper);
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateDTO createDTO) {
        // 检查角色标识是否存在
        if (existsByRoleKey(createDTO.getRoleKey())) {
            throw new RuntimeException(ResultCode.ROLE_EXISTS.getMessage());
        }

        // 检查角色名称是否存在
        if (existsByRoleName(createDTO.getRoleName())) {
            throw new RuntimeException("角色名称已存在");
        }

        // 创建角色
        SysRole role = new SysRole();
        BeanUtils.copyProperties(createDTO, role);
        role.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);
        role.setDataScope(createDTO.getDataScope() != null ? createDTO.getDataScope() : 1);
        role.setSortOrder(createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0);
        
        this.save(role);

        // 分配菜单权限
        if (createDTO.getMenuIds() != null && createDTO.getMenuIds().length > 0) {
            assignMenus(role.getId(), createDTO.getMenuIds());
        }

        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateDTO updateDTO) {
        SysRole existRole = this.getById(updateDTO.getId());
        if (existRole == null) {
            throw new RuntimeException(ResultCode.ROLE_NOT_FOUND.getMessage());
        }

        // 超级管理员角色不能修改
        if (existRole.getId().equals(1L)) {
            throw new RuntimeException("不能修改超级管理员角色");
        }

        // 检查角色名称是否存在
        if (StringUtils.hasText(updateDTO.getRoleName()) && !updateDTO.getRoleName().equals(existRole.getRoleName())) {
            if (existsByRoleName(updateDTO.getRoleName())) {
                throw new RuntimeException("角色名称已存在");
            }
        }

        // 更新角色信息
        SysRole role = new SysRole();
        BeanUtils.copyProperties(updateDTO, role);
        this.updateById(role);

        // 更新菜单权限
        if (updateDTO.getMenuIds() != null) {
            assignMenus(updateDTO.getId(), updateDTO.getMenuIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        if (roleId.equals(1L)) {
            throw new RuntimeException("不能删除超级管理员角色");
        }

        // 检查角色下是否有用户
        if (roleMapper.hasUsers(roleId)) {
            throw new RuntimeException(ResultCode.ROLE_HAS_USERS.getMessage());
        }

        this.removeById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoles(List<Long> roleIds) {
        if (roleIds.contains(1L)) {
            throw new RuntimeException("不能删除超级管理员角色");
        }

        // 检查角色下是否有用户
        for (Long roleId : roleIds) {
            if (roleMapper.hasUsers(roleId)) {
                throw new RuntimeException("角色下存在用户，无法删除");
            }
        }

        this.removeByIds(roleIds);
    }

    @Override
    public void updateStatus(Long roleId, Integer status) {
        if (roleId.equals(1L)) {
            throw new RuntimeException("不能禁用超级管理员角色");
        }
        
        SysRole role = new SysRole();
        role.setId(roleId);
        role.setStatus(status);
        this.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, Long[] menuIds) {
        // 删除原有权限
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        // 这里需要RoleMenuService，暂时省略具体实现
        
        // 添加新权限
        if (menuIds != null && menuIds.length > 0) {
            List<SysRoleMenu> roleMenus = Arrays.stream(menuIds)
                    .map(menuId -> {
                        SysRoleMenu roleMenu = new SysRoleMenu();
                        roleMenu.setRoleId(roleId);
                        roleMenu.setMenuId(menuId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            // 这里需要RoleMenuService，暂时省略具体实现
        }
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public boolean existsByRoleKey(String roleKey) {
        return roleMapper.existsByRoleKey(roleKey);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return roleMapper.existsByRoleName(roleName);
    }

    /**
     * 转换为VO
     */
    private RoleVO convertToVO(SysRole role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        
        // 设置数据权限描述
        vo.setDataScopeDesc(getDataScopeDesc(role.getDataScope()));
        
        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(role.getStatus()));
        
        // 获取菜单权限ID列表
        vo.setMenuIds(getRoleMenuIds(role.getId()));
        
        return vo;
    }

    /**
     * 获取数据权限描述
     */
    private String getDataScopeDesc(Integer dataScope) {
        return switch (dataScope) {
            case 1 -> "全部数据";
            case 2 -> "部门数据";
            case 3 -> "部门及以下数据";
            case 4 -> "仅本人数据";
            default -> "未知";
        };
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        return status == 1 ? "正常" : "禁用";
    }
}

package com.ywhc.admin.modules.system.role.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.modules.system.role.entity.SysRoleMenu;
import com.ywhc.admin.modules.system.role.mapper.SysRoleMenuMapper;
import com.ywhc.admin.modules.system.role.service.SysRoleMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色菜单权限关联服务实现类
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return List.of();
        }
        return sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public List<Long> getRoleIdsByMenuId(Long menuId) {
        if (menuId == null) {
            return List.of();
        }
        return sysRoleMenuMapper.selectRoleIdsByMenuId(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        if (roleId == null || CollectionUtils.isEmpty(menuIds)) {
            return;
        }

        // 先删除原有的角色菜单关联
        removeMenusByRoleId(roleId);

        // 批量插入新的角色菜单关联
        List<SysRoleMenu> roleMenuList = menuIds.stream()
                .map(menuId -> {
                    SysRoleMenu roleMenu = new SysRoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    return roleMenu;
                })
                .collect(Collectors.toList());

        saveBatch(roleMenuList);
        log.info("为角色[{}]分配菜单权限成功，菜单数量：{}", roleId, menuIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMenusByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        remove(wrapper);
        log.info("删除角色[{}]的所有菜单权限成功", roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRolesByMenuId(Long menuId) {
        if (menuId == null) {
            return;
        }
        
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getMenuId, menuId);
        remove(wrapper);
        log.info("删除菜单[{}]的所有角色关联成功", menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMenusByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenu::getRoleId, roleIds);
        remove(wrapper);
        log.info("批量删除角色菜单权限成功，角色数量：{}", roleIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRolesByMenuIds(List<Long> menuIds) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }
        
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenu::getMenuId, menuIds);
        remove(wrapper);
        log.info("批量删除菜单角色关联成功，菜单数量：{}", menuIds.size());
    }

    @Override
    public boolean hasMenuPermission(Long roleId, Long menuId) {
        if (roleId == null || menuId == null) {
            return false;
        }
        
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId)
               .eq(SysRoleMenu::getMenuId, menuId);
        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<SysRoleMenu> roleMenuList) {
        if (CollectionUtils.isEmpty(roleMenuList)) {
            return;
        }
        saveBatch(roleMenuList, roleMenuList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) {
            return;
        }

        // 先删除原有的角色菜单关联
        removeMenusByRoleId(roleId);

        // 如果菜单ID列表不为空，则批量插入新的关联
        if (!CollectionUtils.isEmpty(menuIds)) {
            List<SysRoleMenu> roleMenuList = menuIds.stream()
                    .map(menuId -> {
                        SysRoleMenu roleMenu = new SysRoleMenu();
                        roleMenu.setRoleId(roleId);
                        roleMenu.setMenuId(menuId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            saveBatch(roleMenuList);
        }
        
        log.info("更新角色[{}]的菜单权限成功，菜单数量：{}", roleId, 
                CollectionUtils.isEmpty(menuIds) ? 0 : menuIds.size());
    }
}

package com.ywhc.admin.modules.system.menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.menu.dto.MenuCreateDTO;
import com.ywhc.admin.modules.system.menu.entity.SysMenu;
import com.ywhc.admin.modules.system.menu.vo.MenuTreeVO;
import com.ywhc.admin.modules.system.menu.vo.RouterVO;

import java.util.List;

/**
 * 菜单服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface MenuService extends IService<SysMenu> {

    /**
     * 获取菜单树
     */
    List<MenuTreeVO> getMenuTree();

    /**
     * 根据用户ID获取菜单树
     */
    List<MenuTreeVO> getMenuTreeByUserId(Long userId);

    /**
     * 根据用户ID获取路由
     */
    List<RouterVO> getRoutersByUserId(Long userId);

    /**
     * 创建菜单
     */
    Long createMenu(MenuCreateDTO createDTO);

    /**
     * 更新菜单
     */
    void updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    void deleteMenu(Long menuId);

    /**
     * 修改菜单状态
     */
    void updateStatus(Long menuId, Integer status);

    /**
     * 检查菜单下是否有子菜单
     */
    boolean hasChildren(Long menuId);

    /**
     * 检查菜单是否被角色使用
     */
    boolean isUsedByRole(Long menuId);

    /**
     * 构建菜单树
     */
    List<MenuTreeVO> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建路由树
     */
    List<RouterVO> buildRouters(List<SysMenu> menus);
}

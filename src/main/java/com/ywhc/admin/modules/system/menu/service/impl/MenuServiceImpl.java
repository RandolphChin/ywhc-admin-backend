package com.ywhc.admin.modules.system.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.common.result.ResultCode;
import com.ywhc.admin.modules.system.menu.dto.MenuCreateDTO;
import com.ywhc.admin.modules.system.menu.entity.SysMenu;
import com.ywhc.admin.modules.system.menu.mapper.MenuMapper;
import com.ywhc.admin.modules.system.menu.service.MenuService;
import com.ywhc.admin.modules.system.menu.vo.MenuTreeVO;
import com.ywhc.admin.modules.system.menu.vo.RouterVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 菜单服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl
    extends ServiceImpl<MenuMapper, SysMenu>
    implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<MenuTreeVO> getMenuTree() {
        List<SysMenu> menus = menuMapper.selectMenuTree();
        return buildMenuTree(menus);
    }

    @Override
    public List<MenuTreeVO> getMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus);
    }

    @Override
    public List<RouterVO> getRoutersByUserId(Long userId) {
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        // 只获取目录和菜单类型，过滤掉按钮
        List<SysMenu> routerMenus = menus
            .stream()
            .filter(menu -> menu.getMenuType() != 2)
            .collect(Collectors.toList());
        return buildRouters(routerMenus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(MenuCreateDTO createDTO) {
        // 创建菜单
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(createDTO, menu);
        menu.setStatus(
            createDTO.getStatus() != null ? createDTO.getStatus() : 1
        );
        menu.setSortOrder(
            createDTO.getSortOrder() != null ? createDTO.getSortOrder() : 0
        );
        menu.setIsExternal(
            createDTO.getIsExternal() != null ? createDTO.getIsExternal() : 0
        );
        menu.setIsCache(
            createDTO.getIsCache() != null ? createDTO.getIsCache() : 1
        );
        menu.setIsVisible(
            createDTO.getIsVisible() != null ? createDTO.getIsVisible() : 1
        );

        this.save(menu);
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(SysMenu menu) {
        SysMenu existMenu = this.getById(menu.getId());
        if (existMenu == null) {
            throw new RuntimeException(ResultCode.MENU_NOT_FOUND.getMessage());
        }
        this.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        // 检查是否有子菜单
        if (hasChildren(menuId)) {
            throw new RuntimeException(
                ResultCode.MENU_HAS_CHILDREN.getMessage()
            );
        }

        // 检查是否被角色使用
        if (isUsedByRole(menuId)) {
            throw new RuntimeException("菜单正在使用中，无法删除");
        }

        this.removeById(menuId);
    }

    @Override
    public void updateStatus(Long menuId, Integer status) {
        SysMenu menu = new SysMenu();
        menu.setId(menuId);
        menu.setStatus(status);
        this.updateById(menu);
    }

    @Override
    public boolean hasChildren(Long menuId) {
        return menuMapper.hasChildren(menuId);
    }

    @Override
    public boolean isUsedByRole(Long menuId) {
        return menuMapper.isUsedByRole(menuId);
    }

    @Override
    public List<MenuTreeVO> buildMenuTree(List<SysMenu> menus) {
        List<MenuTreeVO> tree = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (menu.getParentId() == 0) {
                MenuTreeVO menuVO = convertToTreeVO(menu);
                menuVO.setChildren(getChildren(menu.getId(), menus));
                tree.add(menuVO);
            }
        }

        return tree;
    }

    @Override
    public List<RouterVO> buildRouters(List<SysMenu> menus) {
        List<RouterVO> routers = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (menu.getParentId() == 0) {
                RouterVO router = convertToRouter(menu);
                router.setChildren(getRouterChildren(menu.getId(), menus));
                routers.add(router);
            }
        }

        return routers;
    }

    /**
     * 获取子菜单
     */
    private List<MenuTreeVO> getChildren(Long parentId, List<SysMenu> menus) {
        List<MenuTreeVO> children = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                MenuTreeVO menuVO = convertToTreeVO(menu);
                menuVO.setChildren(getChildren(menu.getId(), menus));
                children.add(menuVO);
            }
        }

        return children;
    }

    /**
     * 获取子路由
     */
    private List<RouterVO> getRouterChildren(
        Long parentId,
        List<SysMenu> menus
    ) {
        List<RouterVO> children = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                RouterVO router = convertToRouter(menu);
                router.setChildren(getRouterChildren(menu.getId(), menus));
                children.add(router);
            }
        }

        return children;
    }

    /**
     * 转换为菜单树VO
     */
    private MenuTreeVO convertToTreeVO(SysMenu menu) {
        MenuTreeVO vo = new MenuTreeVO();
        BeanUtils.copyProperties(menu, vo);

        // 设置菜单类型描述
        vo.setMenuTypeDesc(getMenuTypeDesc(menu.getMenuType()));

        // 设置状态描述
        vo.setStatusDesc(getStatusDesc(menu.getStatus()));

        // 设置是否有子菜单
        vo.setHasChildren(hasChildren(menu.getId()));

        return vo;
    }

    /**
     * 转换为路由VO
     */
    private RouterVO convertToRouter(SysMenu menu) {
        RouterVO router = new RouterVO();
        router.setName(menu.getMenuName());
        router.setPath(menu.getPath());
        router.setComponent(menu.getComponent());

        // 设置元信息
        RouterVO.Meta meta = new RouterVO.Meta();
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setHidden(menu.getIsVisible() == 0);
        meta.setKeepAlive(menu.getIsCache() == 1);
        meta.setPermission(menu.getPermission());
        meta.setIsExternal(menu.getIsExternal() == 1);
        meta.setSortOrder(menu.getSortOrder());

        router.setMeta(meta);

        return router;
    }

    /**
     * 获取菜单类型描述
     */
    private String getMenuTypeDesc(Integer menuType) {
        return switch (menuType) {
            case 0 -> "目录";
            case 1 -> "菜单";
            case 2 -> "按钮";
            default -> "未知";
        };
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        return status == 1 ? "正常" : "禁用";
    }

    @Override
    public Map<String, String> getComponentMapping() {
        log.info("开始查询组件映射数据...");

        // 查询所有菜单类型为1（菜单）且有组件路径的记录
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper
            .eq(SysMenu::getMenuType, 1) // 只查询菜单类型
            .isNotNull(SysMenu::getComponent) // 组件路径不为空
            .ne(SysMenu::getComponent, "") // 组件路径不为空字符串
            .eq(SysMenu::getDeleted, 0) // 未删除
            .eq(SysMenu::getStatus, 1) // 状态正常
            .orderByAsc(SysMenu::getSortOrder); // 按排序字段升序

        List<SysMenu> menus = this.list(wrapper);

        // 构建组件映射 Map
        Map<String, String> componentMapping = new HashMap<>();
        for (SysMenu menu : menus) {
            String component = menu.getComponent();
            if (component != null && !component.trim().isEmpty()) {
                // key 和 value 都使用 component 字段值
                // 前端会根据约定构建完整的组件路径
                componentMapping.put(component, component);
                log.debug("添加组件映射: {} -> {}", component, component);
            }
        }

        log.info("组件映射查询完成，共找到 {} 个映射", componentMapping.size());
        return componentMapping;
    }
}

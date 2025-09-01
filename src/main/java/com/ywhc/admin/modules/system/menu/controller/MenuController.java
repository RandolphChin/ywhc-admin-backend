package com.ywhc.admin.modules.system.menu.controller;

import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.modules.system.menu.dto.MenuCreateDTO;
import com.ywhc.admin.modules.system.menu.entity.SysMenu;
import com.ywhc.admin.modules.system.menu.service.MenuService;
import com.ywhc.admin.modules.system.menu.vo.MenuTreeVO;
import com.ywhc.admin.modules.system.menu.vo.RouterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单管理控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "菜单管理", description = "菜单管理相关接口")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<MenuTreeVO>> getMenuTree() {
        List<MenuTreeVO> tree = menuService.getMenuTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取当前用户菜单树")
    @GetMapping("/user-tree")
    public Result<List<MenuTreeVO>> getUserMenuTree() {
        // TODO: 从当前用户上下文获取用户ID
        Long userId = 1L; // 临时使用
        List<MenuTreeVO> tree = menuService.getMenuTreeByUserId(userId);
        return Result.success(tree);
    }

    @Operation(summary = "获取当前用户路由")
    @GetMapping("/routers")
    public Result<List<RouterVO>> getRouters() {
        // TODO: 从当前用户上下文获取用户ID
        Long userId = 1L; // 临时使用
        List<RouterVO> routers = menuService.getRoutersByUserId(userId);
        return Result.success(routers);
    }

    @Operation(summary = "根据ID获取菜单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<SysMenu> getMenuById(
        @Parameter(description = "菜单ID") @PathVariable Long id
    ) {
        SysMenu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @LogAccess(
        value = "创建菜单",
        module = "创建菜单",
        operationType = OperationType.CREATE
    )
    @Operation(summary = "创建菜单")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<Long> createMenu(
        @Valid @RequestBody MenuCreateDTO createDTO
    ) {
        Long menuId = menuService.createMenu(createDTO);
        return Result.success("菜单创建成功", menuId);
    }

    @LogAccess(
        value = "更新菜单",
        module = "更新菜单",
        operationType = OperationType.UPDATE
    )
    @Operation(summary = "更新菜单")
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<String> updateMenu(@Valid @RequestBody SysMenu menu) {
        menuService.updateMenu(menu);
        return Result.success("菜单更新成功");
    }

    @LogAccess(
        value = "删除菜单",
        module = "删除菜单",
        operationType = OperationType.DELETE
    )
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result<String> deleteMenu(
        @Parameter(description = "菜单ID") @PathVariable Long id
    ) {
        menuService.deleteMenu(id);
        return Result.success("菜单删除成功");
    }

    @Operation(summary = "修改菜单状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<String> updateStatus(
        @Parameter(description = "菜单ID") @PathVariable Long id,
        @Parameter(
            description = "状态：0-禁用，1-正常"
        ) @RequestParam Integer status
    ) {
        menuService.updateStatus(id, status);
        return Result.success("菜单状态修改成功");
    }

    @Operation(summary = "获取组件映射配置")
    @GetMapping("/component-mapping")
    public Result<Map<String, String>> getComponentMapping() {
        // 从数据库动态查询组件映射
        Map<String, String> componentMapping =
            menuService.getComponentMapping();
        return Result.success(componentMapping);
    }
}

package com.ywhc.admin.modules.system.role.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.modules.system.role.dto.RoleCreateDTO;
import com.ywhc.admin.modules.system.role.dto.RoleUpdateDTO;
import com.ywhc.admin.modules.system.role.service.RoleService;
import com.ywhc.admin.modules.system.role.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<IPage<RoleVO>> pageRoles(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName,
            @Parameter(description = "角色标识") @RequestParam(required = false) String roleKey,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        IPage<RoleVO> page = roleService.pageRoles(current, size, roleName, roleKey, status);
        return Result.success(page);
    }

    @Operation(summary = "获取所有角色列表")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "根据ID获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<RoleVO> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {
        // TODO: 实现获取角色详情逻辑
        return Result.success();
    }

    @LogAccess(value = "创建角色", module = "角色管理", operationType = OperationType.CREATE)
    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<Long> createRole(@Valid @RequestBody RoleCreateDTO createDTO) {
        Long roleId = roleService.createRole(createDTO);
        return Result.success("角色创建成功", roleId);
    }

    @LogAccess(value = "更新角色", module = "角色管理", operationType = OperationType.UPDATE)
    @Operation(summary = "更新角色")
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<String> updateRole(@Valid @RequestBody RoleUpdateDTO updateDTO) {
        roleService.updateRole(updateDTO);
        return Result.success("角色更新成功");
    }

    @LogAccess(value = "删除角色", module = "角色管理", operationType = OperationType.DELETE)
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<String> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("角色删除成功");
    }

    @LogAccess(value = "批量删除角色", module = "批量删除角色", operationType = OperationType.DELETE)
    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<String> deleteRoles(@RequestBody List<Long> roleIds) {
        roleService.deleteRoles(roleIds);
        return Result.success("角色批量删除成功");
    }

    @Operation(summary = "修改角色状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<String> updateStatus(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-正常") @RequestParam Integer status) {
        roleService.updateStatus(id, status);
        return Result.success("角色状态修改成功");
    }

    @Operation(summary = "分配角色菜单权限")
    @PutMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:auth')")
    public Result<String> assignMenus(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @RequestBody Long[] menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success("权限分配成功");
    }

    @Operation(summary = "获取角色菜单权限")
    @GetMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<List<Long>> getRoleMenus(@Parameter(description = "角色ID") @PathVariable Long id) {
        List<Long> menuIds = roleService.getRoleMenuIds(id);
        return Result.success(menuIds);
    }

    @Operation(summary = "检查角色标识是否存在")
    @GetMapping("/check-role-key")
    public Result<Boolean> checkRoleKey(@Parameter(description = "角色标识") @RequestParam String roleKey) {
        boolean exists = roleService.existsByRoleKey(roleKey);
        return Result.success(exists);
    }

    @Operation(summary = "检查角色名称是否存在")
    @GetMapping("/check-role-name")
    public Result<Boolean> checkRoleName(@Parameter(description = "角色名称") @RequestParam String roleName) {
        boolean exists = roleService.existsByRoleName(roleName);
        return Result.success(exists);
    }
}

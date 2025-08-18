package com.ywhc.admin.modules.system.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.modules.system.user.dto.UserCreateDTO;
import com.ywhc.admin.modules.system.user.dto.UserQueryDTO;
import com.ywhc.admin.modules.system.user.dto.UserUpdateDTO;
import com.ywhc.admin.modules.system.user.entity.SysUser;
import com.ywhc.admin.modules.system.user.service.UserService;
import com.ywhc.admin.modules.system.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<IPage<UserVO>> pageUsers(UserQueryDTO queryDTO) {
        IPage<UserVO> page = userService.pageUsers(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "根据ID获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<SysUser> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        SysUser userVO = userService.getById(id);
        return Result.success(userVO);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Long> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        Long userId = userService.createUser(createDTO);
        return Result.success("用户创建成功", userId);
    }

    @Operation(summary = "更新用户")
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<String> updateUser(@Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return Result.success("用户更新成功");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<String> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("用户删除成功");
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<String> deleteUsers(@RequestBody List<Long> userIds) {
        userService.deleteUsers(userIds);
        return Result.success("用户批量删除成功");
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    public Result<String> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success("密码重置成功");
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<String> updateStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态：0-禁用，1-正常") @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("用户状态修改成功");
    }

    @Operation(summary = "分配用户角色")
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<String> assignRoles(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody Long[] roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success("角色分配成功");
    }

    @Operation(summary = "检查用户名是否存在")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@Parameter(description = "用户名") @RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(exists);
    }

    @Operation(summary = "检查邮箱是否存在")
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@Parameter(description = "邮箱") @RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(exists);
    }

    @Operation(summary = "检查手机号是否存在")
    @GetMapping("/check-mobile")
    public Result<Boolean> checkMobile(@Parameter(description = "手机号") @RequestParam String mobile) {
        boolean exists = userService.existsByMobile(mobile);
        return Result.success(exists);
    }
}

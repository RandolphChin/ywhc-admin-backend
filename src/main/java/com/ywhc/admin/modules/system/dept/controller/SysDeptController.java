package com.ywhc.admin.modules.system.dept.controller;

import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.modules.system.dept.dto.DeptQueryDTO;
import com.ywhc.admin.modules.system.dept.dto.DeptSaveDTO;
import com.ywhc.admin.modules.system.dept.service.SysDeptService;
import com.ywhc.admin.modules.system.dept.vo.DeptTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统部门控制器
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    /**
     * 获取部门树形列表
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:dept:list')")
    @LogAccess(value = "查询部门树形列表", operationType = OperationType.QUERY)
    public Result<List<DeptTreeVO>> getDeptTree(DeptQueryDTO queryDTO) {
        List<DeptTreeVO> tree = deptService.getDeptTree(queryDTO);
        return Result.success(tree);
    }

    /**
     * 获取部门下拉树选择项
     */
    @GetMapping("/tree-select")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<List<DeptTreeVO>> getDeptTreeSelect() {
        List<DeptTreeVO> tree = deptService.getDeptTreeSelect();
        return Result.success(tree);
    }

    /**
     * 根据部门ID获取详细信息
     */
    @GetMapping("/{deptId}")
    @PreAuthorize("hasAuthority('system:dept:query')")
    @LogAccess(value = "查询部门详情", operationType = OperationType.QUERY)
    public Result<DeptTreeVO> getDeptById(@PathVariable Long deptId) {
        DeptTreeVO dept = deptService.getDeptById(deptId);
        return Result.success(dept);
    }

    /**
     * 新增部门
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    @LogAccess(operationType = OperationType.CREATE)
    public Result<Void> saveDept(@Validated @RequestBody DeptSaveDTO saveDTO) {
        boolean result = deptService.saveDept(saveDTO);
        return result ? Result.success() : Result.error("新增部门失败");
    }

    /**
     * 修改部门
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @LogAccess(value = "修改部门", operationType = OperationType.UPDATE)
    public Result<Void> updateDept(@Validated @RequestBody DeptSaveDTO saveDTO) {
        boolean result = deptService.updateDept(saveDTO);
        return result ? Result.success() : Result.error("修改部门失败");
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @LogAccess(value = "删除部门", operationType = OperationType.DELETE)
    public Result<Void> deleteDept(@PathVariable Long deptId) {
        boolean result = deptService.deleteDept(deptId);
        return result ? Result.success() : Result.error("删除部门失败");
    }

    /**
     * 批量删除部门
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @LogAccess(value = "批量删除部门", operationType = OperationType.DELETE)
    public Result<Void> deleteDeptByIds(@RequestBody Long[] deptIds) {
        boolean result = deptService.deleteDeptByIds(deptIds);
        return result ? Result.success() : Result.error("批量删除部门失败");
    }

    /**
     * 校验部门名称是否唯一
     */
    @PostMapping("/check-name")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<Boolean> checkDeptNameUnique(@RequestBody DeptSaveDTO saveDTO) {
        boolean unique = deptService.checkDeptNameUnique(saveDTO);
        return Result.success(unique);
    }

    /**
     * 校验部门编码是否唯一
     */
    @PostMapping("/check-code")
    @PreAuthorize("hasAuthority('system:dept:list')")
    public Result<Boolean> checkDeptCodeUnique(@RequestBody DeptSaveDTO saveDTO) {
        boolean unique = deptService.checkDeptCodeUnique(saveDTO);
        return Result.success(unique);
    }
}

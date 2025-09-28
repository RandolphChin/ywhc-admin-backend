package com.ywhc.admin.modules.test.enterprise.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.modules.test.enterprise.service.EnterpriseService;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseCreateDTO;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseUpdateDTO;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseQueryDTO;
import com.ywhc.admin.modules.test.enterprise.vo.EnterpriseVO;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.annotation.DataPermission;
import com.ywhc.admin.common.enums.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 测试企业控制器
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
@Tag(name = "测试企业管理", description = "测试企业管理相关接口")
@RestController
@RequestMapping("/test/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @LogAccess(value = "查询测试企业列表", module = "测试企业管理", operationType = OperationType.QUERY)
    @Operation(summary = "分页查询测试企业列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('test:enterprise:list')")
    // @DataPermission(deptIdColumn = "dept_id")  数据权限
    public Result<IPage<EnterpriseVO>> pageEnterprises(EnterpriseQueryDTO dto) {
        IPage<EnterpriseVO> page = enterpriseService.pageEnterprises(dto);
        return Result.success(page);
    }

    @LogAccess(value = "查询测试企业列表", module = "测试企业管理", operationType = OperationType.QUERY)
    @Operation(summary = "查询所有测试企业列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('test:enterprise:list')")
    // @DataPermission(deptIdColumn = "dept_id")
    public Result<List<EnterpriseVO>> listEnterprises(EnterpriseQueryDTO dto) {
        List<EnterpriseVO> list = enterpriseService.listEnterprises(dto);
        return Result.success(list);
    }

    @LogAccess(value = "查询测试企业详情", module = "测试企业管理", operationType = OperationType.QUERY)
    @Operation(summary = "根据ID查询测试企业详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('test:enterprise:query')")
    public Result<EnterpriseVO> getEnterprise(@PathVariable Long id) {
        EnterpriseVO enterpriseVO = enterpriseService.getEnterpriseById(id);
        return Result.success(enterpriseVO);
    }

    @LogAccess(value = "新增测试企业", module = "测试企业管理", operationType = OperationType.CREATE)
    @Operation(summary = "新增测试企业")
    @PostMapping
    @PreAuthorize("hasAuthority('test:enterprise:add')")
    public Result<String> createEnterprise(@Valid @RequestBody EnterpriseCreateDTO dto) {
        enterpriseService.createEnterprise(dto);
        return Result.success("测试企业创建成功");
    }

    @LogAccess(value = "修改测试企业", module = "测试企业管理", operationType = OperationType.UPDATE)
    @Operation(summary = "修改测试企业")
    @PutMapping
    @PreAuthorize("hasAuthority('test:enterprise:edit')")
    public Result<String> updateEnterprise(@Valid @RequestBody EnterpriseUpdateDTO dto) {
        enterpriseService.updateEnterprise(dto);
        return Result.success("测试企业更新成功");
    }

    @LogAccess(value = "删除测试企业", module = "测试企业管理", operationType = OperationType.DELETE)
    @Operation(summary = "删除测试企业")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('test:enterprise:remove')")
    public Result<String> deleteEnterprise(@PathVariable Long id) {
        enterpriseService.deleteEnterprise(id);
        return Result.success("测试企业删除成功");
    }

    @LogAccess(value = "批量删除测试企业", module = "测试企业管理", operationType = OperationType.DELETE)
    @Operation(summary = "批量删除测试企业")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('test:enterprise:remove')")
    public Result<String> deleteEnterprises(@RequestBody List<Long> ids) {
        enterpriseService.deleteEnterprise(ids);
        return Result.success("测试企业批量删除成功");
    }

    @LogAccess(value = "导出测试企业", module = "测试企业管理", operationType = OperationType.EXPORT)
    @Operation(summary = "导出测试企业")
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('test:enterprise:export')")
    public ResponseEntity<byte[]> exportEnterprises(@RequestBody EnterpriseQueryDTO dto) {
        byte[] data = enterpriseService.exportEnterprises(dto);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"测试企业_" + System.currentTimeMillis() + ".xlsx\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(data);
    }
}

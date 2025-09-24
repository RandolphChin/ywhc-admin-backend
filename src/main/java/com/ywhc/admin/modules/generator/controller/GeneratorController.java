package com.ywhc.admin.modules.generator.controller;

import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.modules.generator.dto.GeneratorConfigDTO;
import com.ywhc.admin.modules.generator.entity.TableInfo;
import com.ywhc.admin.modules.generator.service.GeneratorService;
import com.ywhc.admin.modules.generator.vo.GeneratedCodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码生成控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "代码生成", description = "代码生成相关接口")
@RestController
@RequestMapping("/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;

    @LogAccess(value = "查询数据库表列表", module = "代码生成", operationType = OperationType.QUERY)
    @Operation(summary = "获取数据库表列表")
    @GetMapping("/tables")
    @PreAuthorize("hasAuthority('generator:table:list')")
    public Result<List<TableInfo>> getTableList() {
        List<TableInfo> tables = generatorService.getTableList();
        return Result.success(tables);
    }

    @LogAccess(value = "查询表详细信息", module = "代码生成", operationType = OperationType.QUERY)
    @Operation(summary = "获取表详细信息")
    @GetMapping("/tables/{tableName}")
    @PreAuthorize("hasAuthority('generator:table:info')")
    public Result<TableInfo> getTableInfo(@PathVariable String tableName) {
        TableInfo tableInfo = generatorService.getTableInfo(tableName);
        return Result.success(tableInfo);
    }

    @LogAccess(value = "预览生成代码", module = "代码生成", operationType = OperationType.QUERY)
    @Operation(summary = "预览生成代码")
    @PostMapping("/preview")
    @PreAuthorize("hasAuthority('generator:code:preview')")
    public Result<GeneratedCodeVO> previewCode(@RequestBody GeneratorConfigDTO config) {
        GeneratedCodeVO result = generatorService.previewCode(config);
        return Result.success(result);
    }

    @LogAccess(value = "生成并下载代码", module = "代码生成", operationType = OperationType.EXPORT)
    @Operation(summary = "生成并下载代码")
    @PostMapping("/download")
    @PreAuthorize("hasAuthority('generator:code:download')")
    public ResponseEntity<byte[]> downloadCode(@RequestBody GeneratorConfigDTO config) {
        byte[] zipData = generatorService.generateCode(config);

        String fileName = config.getBusinessName() + "_code.zip";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipData);
    }
}
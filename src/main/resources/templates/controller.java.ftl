package ${package.Controller};

import com.baomidou.mybatisplus.core.metadata.IPage;
import ${package.Service}.${table.serviceName};
import ${package.Entity}.${entity};
import ${package.Parent}.dto.${entity}QueryDTO;
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
 * ${table.comment!}控制器
 *
 * @author ${author}
 * @since ${date}
 */
@Tag(name = "${table.comment!}管理", description = "${table.comment!}管理相关接口")
@RestController
@RequestMapping("/${cfg.moduleName}/${cfg.businessName}")
@RequiredArgsConstructor
public class ${table.controllerName} {

    private final ${table.serviceName} ${table.serviceName?uncap_first};

    @LogAccess(value = "查询${table.comment!}列表", module = "${table.comment!}管理", operationType = OperationType.QUERY)
    @Operation(summary = "分页查询${table.comment!}列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:list')")
    @DataPermission(deptIdColumn = "dept_id")
    public Result<IPage<${entity}>> page${entity}s(${entity}QueryDTO queryDTO) {
        IPage<${entity}> page = ${table.serviceName?uncap_first}.page${entity}s(queryDTO);
        return Result.success(page);
    }

    @LogAccess(value = "查询${table.comment!}列表", module = "${table.comment!}管理", operationType = OperationType.QUERY)
    @Operation(summary = "查询所有${table.comment!}列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:list')")
    @DataPermission(deptIdColumn = "dept_id")
    public Result<List<${entity}>> list${entity}s(${entity}QueryDTO queryDTO) {
        List<${entity}> list = ${table.serviceName?uncap_first}.list${entity}s(queryDTO);
        return Result.success(list);
    }

    @LogAccess(value = "查询${table.comment!}详情", module = "${table.comment!}管理", operationType = OperationType.QUERY)
    @Operation(summary = "根据ID查询${table.comment!}详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:query')")
    public Result<${entity}> get${entity}(@PathVariable Long id) {
        ${entity} ${entity?uncap_first} = ${table.serviceName?uncap_first}.getById(id);
        return Result.success(${entity?uncap_first});
    }

    @LogAccess(value = "新增${table.comment!}", module = "${table.comment!}管理", operationType = OperationType.INSERT)
    @Operation(summary = "新增${table.comment!}")
    @PostMapping
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:add')")
    public Result<String> create${entity}(@Valid @RequestBody ${entity} ${entity?uncap_first}) {
        ${table.serviceName?uncap_first}.save(${entity?uncap_first});
        return Result.success("${table.comment!}创建成功");
    }

    @LogAccess(value = "修改${table.comment!}", module = "${table.comment!}管理", operationType = OperationType.UPDATE)
    @Operation(summary = "修改${table.comment!}")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:edit')")
    public Result<String> update${entity}(@PathVariable Long id, @Valid @RequestBody ${entity} ${entity?uncap_first}) {
        ${entity?uncap_first}.setId(id);
        ${table.serviceName?uncap_first}.updateById(${entity?uncap_first});
        return Result.success("${table.comment!}更新成功");
    }

    @LogAccess(value = "删除${table.comment!}", module = "${table.comment!}管理", operationType = OperationType.DELETE)
    @Operation(summary = "删除${table.comment!}")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:remove')")
    public Result<String> delete${entity}(@PathVariable Long id) {
        ${table.serviceName?uncap_first}.removeById(id);
        return Result.success("${table.comment!}删除成功");
    }

    @LogAccess(value = "批量删除${table.comment!}", module = "${table.comment!}管理", operationType = OperationType.DELETE)
    @Operation(summary = "批量删除${table.comment!}")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:remove')")
    public Result<String> delete${entity}s(@RequestBody List<Long> ids) {
        ${table.serviceName?uncap_first}.removeByIds(ids);
        return Result.success("${table.comment!}批量删除成功");
    }

    @LogAccess(value = "导出${table.comment!}", module = "${table.comment!}管理", operationType = OperationType.EXPORT)
    @Operation(summary = "导出${table.comment!}")
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('${cfg.moduleName}:${cfg.businessName}:export')")
    public ResponseEntity<byte[]> export${entity}s(@RequestBody ${entity}QueryDTO queryDTO) {
        byte[] data = ${table.serviceName?uncap_first}.export${entity}s(queryDTO);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${table.comment!}_" + System.currentTimeMillis() + ".xlsx\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(data);
    }
}
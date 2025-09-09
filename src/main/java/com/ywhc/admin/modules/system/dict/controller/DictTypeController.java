package com.ywhc.admin.modules.system.dict.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.common.util.ObjectConverter;
import com.ywhc.admin.modules.system.dict.dto.DictTypeCreateDTO;
import com.ywhc.admin.modules.system.dict.dto.DictTypeQueryDTO;
import com.ywhc.admin.modules.system.dict.dto.DictTypeUpdateDTO;
import com.ywhc.admin.modules.system.dict.entity.SysDictType;
import com.ywhc.admin.modules.system.dict.service.DictTypeService;
import com.ywhc.admin.modules.system.dict.vo.DictTypeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型管理控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "字典类型管理", description = "字典类型管理相关接口")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    @Operation(summary = "分页查询字典类型列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<IPage<DictTypeVO>> pageDictTypes(DictTypeQueryDTO queryDTO) {
        IPage<DictTypeVO> page = dictTypeService.pageDictTypes(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "获取所有字典类型列表")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<List<DictTypeVO>> getAllDictTypes() {
        List<DictTypeVO> dictTypes = dictTypeService.getAllDictTypes();
        return Result.success(dictTypes);
    }

    @Operation(summary = "根据ID获取字典类型详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<DictTypeVO> getDictTypeById(@Parameter(description = "字典类型ID") @PathVariable Long id) {
        SysDictType dictType = dictTypeService.getById(id);
        DictTypeVO dictTypeVO = ObjectConverter.convert(dictType, DictTypeVO.class);
        return Result.success(dictTypeVO);
    }

    @LogAccess(value = "创建字典类型", module = "字典管理", operationType = OperationType.CREATE)
    @Operation(summary = "创建字典类型")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Long> createDictType(@Valid @RequestBody DictTypeCreateDTO createDTO) {
        Long dictTypeId = dictTypeService.createDictType(createDTO);
        return Result.success("字典类型创建成功", dictTypeId);
    }

    @LogAccess(value = "更新字典类型", module = "字典管理", operationType = OperationType.UPDATE)
    @Operation(summary = "更新字典类型")
    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<String> updateDictType(@Valid @RequestBody DictTypeUpdateDTO updateDTO) {
        dictTypeService.updateDictType(updateDTO);
        return Result.success("字典类型更新成功");
    }

    @LogAccess(value = "删除字典类型", module = "字典管理", operationType = OperationType.DELETE)
    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<String> deleteDictType(@Parameter(description = "字典类型ID") @PathVariable Long id) {
        dictTypeService.deleteDictType(id);
        return Result.success("字典类型删除成功");
    }

    @LogAccess(value = "批量删除字典类型", module = "字典管理", operationType = OperationType.DELETE)
    @Operation(summary = "批量删除字典类型")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<String> deleteDictTypes(@RequestBody List<Long> ids) {
        dictTypeService.deleteDictTypes(ids);
        return Result.success("字典类型批量删除成功");
    }

    @Operation(summary = "检查字典类型是否存在")
    @GetMapping("/check-dict-type")
    public Result<Boolean> checkDictType(@Parameter(description = "字典类型") @RequestParam String dictType) {
        boolean exists = dictTypeService.existsByDictType(dictType);
        return Result.success(exists);
    }

    @LogAccess(value = "刷新字典缓存", module = "字典管理", operationType = OperationType.UPDATE)
    @Operation(summary = "刷新字典缓存")
    @PostMapping("/refresh-cache")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<String> refreshCache() {
        dictTypeService.refreshCache();
        return Result.success("字典缓存刷新成功");
    }
}

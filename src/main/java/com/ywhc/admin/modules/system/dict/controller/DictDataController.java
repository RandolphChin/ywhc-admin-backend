package com.ywhc.admin.modules.system.dict.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.common.result.Result;
import com.ywhc.admin.common.annotation.LogAccess;
import com.ywhc.admin.common.enums.OperationType;
import com.ywhc.admin.common.util.ObjectConverter;
import com.ywhc.admin.modules.system.dict.dto.DictDataCreateDTO;
import com.ywhc.admin.modules.system.dict.dto.DictDataQueryDTO;
import com.ywhc.admin.modules.system.dict.dto.DictDataUpdateDTO;
import com.ywhc.admin.modules.system.dict.entity.SysDictData;
import com.ywhc.admin.modules.system.dict.service.DictDataService;
import com.ywhc.admin.modules.system.dict.vo.DictDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理控制器
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Tag(name = "字典数据管理", description = "字典数据管理相关接口")
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    @Operation(summary = "分页查询字典数据列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<IPage<DictDataVO>> pageDictData(DictDataQueryDTO queryDTO) {
        IPage<DictDataVO> page = dictDataService.pageDictData(queryDTO);
        return Result.success(page);
    }

    @Operation(summary = "根据字典类型获取字典数据列表")
    @GetMapping("/type/{dictType}")
    public Result<List<DictDataVO>> getDictDataByType(@Parameter(description = "字典类型") @PathVariable String dictType) {
        List<DictDataVO> dictDataList = dictDataService.getDictDataByType(dictType);
        return Result.success(dictDataList);
    }

    @Operation(summary = "根据字典类型和字典值获取字典标签")
    @GetMapping("/label")
    public Result<String> getDictLabel(
            @Parameter(description = "字典类型") @RequestParam String dictType,
            @Parameter(description = "字典值") @RequestParam String dictValue) {
        String label = dictDataService.getDictLabel(dictType, dictValue);
        return Result.success(label);
    }

    @Operation(summary = "根据ID获取字典数据详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:list')")
    public Result<DictDataVO> getDictDataById(@Parameter(description = "字典数据ID") @PathVariable Long id) {
        SysDictData data = dictDataService.getById(id);
        DictDataVO dictData = ObjectConverter.convert(data, DictDataVO.class);
        return Result.success(dictData);
    }

    @LogAccess(value = "创建字典数据", module = "字典管理", operationType = OperationType.CREATE)
    @Operation(summary = "创建字典数据")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Long> createDictData(@Valid @RequestBody DictDataCreateDTO createDTO) {
        Long dictDataId = dictDataService.createDictData(createDTO);
        return Result.success("字典数据创建成功", dictDataId);
    }

    @LogAccess(value = "更新字典数据", module = "字典管理", operationType = OperationType.UPDATE)
    @Operation(summary = "更新字典数据")
    @PutMapping
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<String> updateDictData(@Valid @RequestBody DictDataUpdateDTO updateDTO) {
        dictDataService.updateDictData(updateDTO);
        return Result.success("字典数据更新成功");
    }

    @LogAccess(value = "删除字典数据", module = "字典管理", operationType = OperationType.DELETE)
    @Operation(summary = "删除字典数据")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<String> deleteDictData(@Parameter(description = "字典数据ID") @PathVariable Long id) {
        dictDataService.deleteDictData(id);
        return Result.success("字典数据删除成功");
    }

    @LogAccess(value = "批量删除字典数据", module = "字典管理", operationType = OperationType.DELETE)
    @Operation(summary = "批量删除字典数据")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:dict:delete')")
    public Result<String> deleteDictData(@RequestBody List<Long> ids) {
        dictDataService.deleteDictData(ids);
        return Result.success("字典数据批量删除成功");
    }

    @Operation(summary = "检查字典值是否存在")
    @GetMapping("/check-dict-value")
    public Result<Boolean> checkDictValue(
            @Parameter(description = "字典类型") @RequestParam String dictType,
            @Parameter(description = "字典值") @RequestParam String dictValue) {
        boolean exists = dictDataService.existsByDictTypeAndValue(dictType, dictValue);
        return Result.success(exists);
    }
}

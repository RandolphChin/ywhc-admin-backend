package com.ywhc.admin.modules.system.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.modules.system.dict.dto.DictTypeCreateDTO;
import com.ywhc.admin.modules.system.dict.dto.DictTypeQueryDTO;
import com.ywhc.admin.modules.system.dict.dto.DictTypeUpdateDTO;
import com.ywhc.admin.modules.system.dict.entity.SysDictType;
import com.ywhc.admin.modules.system.dict.mapper.DictTypeMapper;
import com.ywhc.admin.modules.system.dict.service.DictDataService;
import com.ywhc.admin.modules.system.dict.service.DictTypeService;
import com.ywhc.admin.modules.system.dict.vo.DictTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典类型服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, SysDictType> implements DictTypeService {

    private final DictDataService dictDataService;

    @Override
    public IPage<DictTypeVO> pageDictTypes(DictTypeQueryDTO queryDTO) {
        Page<SysDictType> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getDictNameLike()), SysDictType::getDictName, queryDTO.getDictNameLike())
               .like(StringUtils.hasText(queryDTO.getDictTypeLike()), SysDictType::getDictType, queryDTO.getDictTypeLike())
               ;

        // 排序
        if (StringUtils.hasText(queryDTO.getOrderBy())) {
            if ("desc".equalsIgnoreCase(queryDTO.getOrderDirection())) {
               // wrapper.orderByDesc(getColumnByField(queryDTO.getOrderBy()));
            } else {
              //  wrapper.orderByAsc(getColumnByField(queryDTO.getOrderBy()));
            }
        } else {
           // wrapper.orderByDesc(SysDictType::getCreateTime);
        }

        IPage<SysDictType> dictTypePage = this.page(page, wrapper);

        // 转换为VO
        IPage<DictTypeVO> voPage = new Page<>(dictTypePage.getCurrent(), dictTypePage.getSize(), dictTypePage.getTotal());
        List<DictTypeVO> voList = dictTypePage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<DictTypeVO> getAllDictTypes() {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper//.eq(SysDictType::getStatus, 1)
               .orderByAsc(SysDictType::getDictType);

        List<SysDictType> dictTypes = this.list(wrapper);
        return dictTypes.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public SysDictType getByDictType(String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDictType(DictTypeCreateDTO createDTO) {
        // 检查字典类型是否已存在
        if (existsByDictType(createDTO.getDictType())) {
            throw new RuntimeException("字典类型已存在");
        }

        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(createDTO, dictType);

        this.save(dictType);
        return dictType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictType(DictTypeUpdateDTO updateDTO) {
        SysDictType existingDictType = this.getById(updateDTO.getId());
        if (existingDictType == null) {
            throw new RuntimeException("字典类型不存在");
        }

        // 如果修改了字典类型，检查新的字典类型是否已存在
        if (!existingDictType.getDictType().equals(updateDTO.getDictType()) &&
            existsByDictType(updateDTO.getDictType())) {
            throw new RuntimeException("字典类型已存在");
        }

        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(updateDTO, dictType);

        this.updateById(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictType(Long id) {
        SysDictType dictType = this.getById(id);
        if (dictType == null) {
            throw new RuntimeException("字典类型不存在");
        }

        // 删除字典类型时，同时删除对应的字典数据
        dictDataService.deleteDictDataByType(dictType.getDictType());

        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypes(List<Long> ids) {
        for (Long id : ids) {
            deleteDictType(id);
        }
    }

    @Override
    public boolean existsByDictType(String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        return this.count(wrapper) > 0;
    }

    @Override
    public void refreshCache() {
        // TODO: 实现缓存刷新逻辑
    }

    private DictTypeVO convertToVO(SysDictType dictType) {
        DictTypeVO vo = new DictTypeVO();
        BeanUtils.copyProperties(dictType, vo);
        return vo;
    }

    private String getColumnByField(String field) {
        switch (field) {
            case "dictName":
                return "dict_name";
            case "dictType":
                return "dict_type";
            case "status":
                return "status";
            case "createTime":
                return "create_time";
            case "updateTime":
                return "update_time";
            default:
                return "create_time";
        }
    }
}

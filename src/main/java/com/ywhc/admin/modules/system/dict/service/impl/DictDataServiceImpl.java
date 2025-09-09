package com.ywhc.admin.modules.system.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.modules.system.dict.dto.DictDataCreateDTO;
import com.ywhc.admin.modules.system.dict.dto.DictDataQueryDTO;
import com.ywhc.admin.modules.system.dict.dto.DictDataUpdateDTO;
import com.ywhc.admin.modules.system.dict.entity.SysDictData;
import com.ywhc.admin.modules.system.dict.mapper.DictDataMapper;
import com.ywhc.admin.modules.system.dict.service.DictDataService;
import com.ywhc.admin.modules.system.dict.vo.DictDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典数据服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, SysDictData> implements DictDataService {

    @Override
    public IPage<DictDataVO> pageDictData(DictDataQueryDTO queryDTO) {
        Page<SysDictData> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());

        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getDictType()), SysDictData::getDictType, queryDTO.getDictType())
               .like(StringUtils.hasText(queryDTO.getDictLabelLike()), SysDictData::getDictLabel, queryDTO.getDictLabelLike())
               .like(StringUtils.hasText(queryDTO.getDictValueLike()), SysDictData::getDictValue, queryDTO.getDictValueLike())
               .eq(queryDTO.getStatus() != null, SysDictData::getStatus, queryDTO.getStatus());

        // 排序
        if (StringUtils.hasText(queryDTO.getOrderBy())) {
            if ("desc".equalsIgnoreCase(queryDTO.getOrderDirection())) {
              //  wrapper.orderByDesc(getColumnByField(queryDTO.getOrderBy()));
            } else {
             //   wrapper.orderByAsc(getColumnByField(queryDTO.getOrderBy()));
            }
        } else {
            wrapper.orderByAsc(SysDictData::getDictSort);
        }

        IPage<SysDictData> dictDataPage = this.page(page, wrapper);

        // 转换为VO
        IPage<DictDataVO> voPage = new Page<>(dictDataPage.getCurrent(), dictDataPage.getSize(), dictDataPage.getTotal());
        List<DictDataVO> voList = dictDataPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public List<DictDataVO> getDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getStatus, 1)
               .orderByAsc(SysDictData::getDictSort);

        List<SysDictData> dictDataList = this.list(wrapper);
        return dictDataList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public String getDictLabel(String dictType, String dictValue) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getDictValue, dictValue)
               .eq(SysDictData::getStatus, 1);

        SysDictData dictData = this.getOne(wrapper);
        return dictData != null ? dictData.getDictLabel() : dictValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDictData(DictDataCreateDTO createDTO) {
        // 检查字典值是否已存在
        if (existsByDictTypeAndValue(createDTO.getDictType(), createDTO.getDictValue())) {
            throw new RuntimeException("字典值已存在");
        }

        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(createDTO, dictData);

        this.save(dictData);
        return dictData.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(DictDataUpdateDTO updateDTO) {
        SysDictData existingDictData = this.getById(updateDTO.getId());
        if (existingDictData == null) {
            throw new RuntimeException("字典数据不存在");
        }

        // 如果修改了字典值，检查新的字典值是否已存在
        if (!existingDictData.getDictValue().equals(updateDTO.getDictValue()) &&
            existsByDictTypeAndValue(updateDTO.getDictType(), updateDTO.getDictValue())) {
            throw new RuntimeException("字典值已存在");
        }

        SysDictData dictData = new SysDictData();
        BeanUtils.copyProperties(updateDTO, dictData);

        this.updateById(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictDataByType(String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        this.remove(wrapper);
    }

    @Override
    public boolean existsByDictTypeAndValue(String dictType, String dictValue) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
               .eq(SysDictData::getDictValue, dictValue);
        return this.count(wrapper) > 0;
    }

    private DictDataVO convertToVO(SysDictData dictData) {
        DictDataVO vo = new DictDataVO();
        BeanUtils.copyProperties(dictData, vo);
        return vo;
    }

    private String getColumnByField(String field) {
        switch (field) {
            case "dictSort":
                return "dict_sort";
            case "dictLabel":
                return "dict_label";
            case "dictValue":
                return "dict_value";
            case "dictType":
                return "dict_type";
            case "status":
                return "status";
            case "createTime":
                return "create_time";
            case "updateTime":
                return "update_time";
            default:
                return "dict_sort";
        }
    }
}

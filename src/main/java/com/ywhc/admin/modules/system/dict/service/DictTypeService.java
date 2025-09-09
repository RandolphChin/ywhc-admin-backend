package com.ywhc.admin.modules.system.dict.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.dict.dto.DictTypeCreateDTO;
import com.ywhc.admin.modules.system.dict.dto.DictTypeQueryDTO;
import com.ywhc.admin.modules.system.dict.dto.DictTypeUpdateDTO;
import com.ywhc.admin.modules.system.dict.entity.SysDictType;
import com.ywhc.admin.modules.system.dict.vo.DictTypeVO;

import java.util.List;

/**
 * 字典类型服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface DictTypeService extends IService<SysDictType> {

    /**
     * 分页查询字典类型列表
     */
    IPage<DictTypeVO> pageDictTypes(DictTypeQueryDTO queryDTO);

    /**
     * 获取所有字典类型列表
     */
    List<DictTypeVO> getAllDictTypes();

    /**
     * 根据字典类型获取字典类型信息
     */
    SysDictType getByDictType(String dictType);

    /**
     * 创建字典类型
     */
    Long createDictType(DictTypeCreateDTO createDTO);

    /**
     * 更新字典类型
     */
    void updateDictType(DictTypeUpdateDTO updateDTO);

    /**
     * 删除字典类型
     */
    void deleteDictType(Long id);

    /**
     * 批量删除字典类型
     */
    void deleteDictTypes(List<Long> ids);

    /**
     * 检查字典类型是否存在
     */
    boolean existsByDictType(String dictType);

    /**
     * 刷新字典缓存
     */
    void refreshCache();
}

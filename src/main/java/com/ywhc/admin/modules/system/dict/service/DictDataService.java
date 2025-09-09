package com.ywhc.admin.modules.system.dict.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.dict.dto.DictDataCreateDTO;
import com.ywhc.admin.modules.system.dict.dto.DictDataQueryDTO;
import com.ywhc.admin.modules.system.dict.dto.DictDataUpdateDTO;
import com.ywhc.admin.modules.system.dict.entity.SysDictData;
import com.ywhc.admin.modules.system.dict.vo.DictDataVO;

import java.util.List;

/**
 * 字典数据服务接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface DictDataService extends IService<SysDictData> {

    /**
     * 分页查询字典数据列表
     */
    IPage<DictDataVO> pageDictData(DictDataQueryDTO queryDTO);

    /**
     * 根据字典类型获取字典数据列表
     */
    List<DictDataVO> getDictDataByType(String dictType);

    /**
     * 根据字典类型和字典值获取字典标签
     */
    String getDictLabel(String dictType, String dictValue);

    /**
     * 创建字典数据
     */
    Long createDictData(DictDataCreateDTO createDTO);

    /**
     * 更新字典数据
     */
    void updateDictData(DictDataUpdateDTO updateDTO);

    /**
     * 删除字典数据
     */
    void deleteDictData(Long id);

    /**
     * 批量删除字典数据
     */
    void deleteDictData(List<Long> ids);

    /**
     * 根据字典类型删除字典数据
     */
    void deleteDictDataByType(String dictType);

    /**
     * 检查字典值是否存在
     */
    boolean existsByDictTypeAndValue(String dictType, String dictValue);
}

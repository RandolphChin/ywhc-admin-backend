package com.ywhc.admin.modules.generator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ywhc.admin.modules.generator.dto.GeneratorConfigDTO;
import com.ywhc.admin.modules.generator.dto.GeneratorQueryDTO;
import com.ywhc.admin.modules.generator.entity.TableInfo;
import com.ywhc.admin.modules.generator.vo.GeneratedCodeVO;

/**
 * 代码生成服务接口
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface GeneratorService {
    
    /**
     * 分页查询数据库表列表
     * @param queryDTO 查询条件
     */
    IPage<TableInfo> getTableList(GeneratorQueryDTO queryDTO);
    
    /**
     * 获取表详细信息
     */
    TableInfo getTableInfo(String tableName);
    
    /**
     * 预览生成代码
     */
    GeneratedCodeVO previewCode(GeneratorConfigDTO config);
    
    /**
     * 生成代码并打包下载
     */
    byte[] generateCode(GeneratorConfigDTO config);
}
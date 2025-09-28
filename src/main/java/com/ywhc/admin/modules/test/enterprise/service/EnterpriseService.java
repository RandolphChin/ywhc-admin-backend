package com.ywhc.admin.modules.test.enterprise.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.test.enterprise.entity.Enterprise;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseCreateDTO;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseUpdateDTO;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseQueryDTO;
import com.ywhc.admin.modules.test.enterprise.vo.EnterpriseVO;
import java.util.List;

/**
 * 测试企业服务接口
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
public interface EnterpriseService extends IService<Enterprise> {

    /**
     * 分页查询测试企业
     */
    IPage<EnterpriseVO> pageEnterprises(EnterpriseQueryDTO dto);

    /**
     * 查询所有测试企业列表
     */
    List<EnterpriseVO> listEnterprises(EnterpriseQueryDTO dto);

    /**
     * 导出测试企业
     */
    byte[] exportEnterprises(EnterpriseQueryDTO dto);

    /**
     * 创建测试企业
     */
    Long createEnterprise(EnterpriseCreateDTO dto);

    /**
     * 更新测试企业
     */
    void updateEnterprise(EnterpriseUpdateDTO dto);

    /**
     * 删除测试企业
     */
    void deleteEnterprise(Long id);

    /**
     * 批量删除测试企业
     */
    void deleteEnterprise(List<Long> ids);

    /**
     * 根据ID获取测试企业详情
     */
    EnterpriseVO getEnterpriseById(Long id);
}
package com.ywhc.admin.modules.test.enterprise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.test.enterprise.entity.Enterprise;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测试企业Mapper接口
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
@Mapper
public interface EnterpriseMapper extends BaseMapper<Enterprise> {

}
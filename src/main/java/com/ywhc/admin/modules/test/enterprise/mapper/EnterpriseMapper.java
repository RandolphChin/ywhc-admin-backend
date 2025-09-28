package com.ywhc.admin.modules.test.enterprise.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.test.enterprise.entity.Enterprise;
import com.ywhc.admin.modules.test.enterprise.vo.EnterpriseVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 测试企业Mapper接口
 *
 * @author YWHC Team
 * @since 2025-09-28
 */
@Mapper
public interface EnterpriseMapper extends BaseMapper<Enterprise> {
    // 分页查询
    IPage<EnterpriseVO> pageJoin(Page page, @Param(Constants.WRAPPER) QueryWrapper wrapper);
    List<EnterpriseVO> listJoin(@Param(Constants.WRAPPER) QueryWrapper wrapper);
}

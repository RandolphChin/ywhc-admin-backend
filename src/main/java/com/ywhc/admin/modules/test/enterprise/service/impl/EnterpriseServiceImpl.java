package com.ywhc.admin.modules.test.enterprise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.common.util.SecurityUtils;
import com.ywhc.admin.modules.test.enterprise.entity.Enterprise;
import com.ywhc.admin.modules.test.enterprise.mapper.EnterpriseMapper;
import com.ywhc.admin.modules.test.enterprise.service.EnterpriseService;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseCreateDTO;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseUpdateDTO;
import com.ywhc.admin.modules.test.enterprise.dto.EnterpriseQueryDTO;
import com.ywhc.admin.modules.test.enterprise.vo.EnterpriseVO;
import com.ywhc.admin.common.util.PageConverter;
import com.ywhc.admin.common.util.QueryProcessor;
import com.ywhc.admin.common.util.ListConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.core.date.DateUtil;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 测试企业服务实现类
 *
 * @author YWHC Team
 * @since 2025-09-28
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper, Enterprise> implements EnterpriseService {
    private final EnterpriseMapper enterpriseMapper;
    @Override
    public IPage<EnterpriseVO> pageEnterprises(EnterpriseQueryDTO dto) {
        Page<Enterprise> page = new Page<>(dto.getCurrent(), dto.getSize());
        //Page<Enterprise> entityPage = this.page(page, QueryProcessor.createQueryWrapper(dto));
        //Page<EnterpriseVO> pageVO = PageConverter.convert(entityPage, this::convertToVO);
        IPage<EnterpriseVO> pageVO = enterpriseMapper.pageJoin(page, QueryProcessor.createQueryWrapper(dto));
        return pageVO;
    }

    @Override
    public List<EnterpriseVO> listEnterprises(EnterpriseQueryDTO dto) {
        QueryWrapper<Enterprise> queryWrapper = QueryProcessor.createQueryWrapper(dto);
        List<EnterpriseVO> list = enterpriseMapper.listJoin(queryWrapper);
        return list;
    }

    @Override
    public byte[] exportEnterprises(EnterpriseQueryDTO dto) {
        List<EnterpriseVO> list = listEnterprises(dto);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 创建Excel写入器
            ExcelWriter writer = ExcelUtil.getWriter(true);

            // 设置表头
            Map<String, String> headerMap = new LinkedHashMap<>();
            headerMap.put("enterpriseName", "企业名称");
            headerMap.put("enterpriseAddress", "企业地址");
            headerMap.put("deptId", "数据权限-当前用户所在部门");
            headerMap.put("status", "状态：0-禁用，1-正常");
            headerMap.put("deleted", "删除标志：0-正常，1-删除");
            headerMap.put("createTime", "创建时间");
            headerMap.put("updateTime", "更新时间");
            headerMap.put("createBy", "创建者");
            headerMap.put("updateBy", "更新者");

            // 写入表头
            writer.writeHeadRow(new ArrayList<>(headerMap.values()));

            // 转换数据为Map列表
            List<Map<String, Object>> rows = new ArrayList<>();
            for (EnterpriseVO vo : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("企业名称", vo.getEnterpriseName());
                row.put("企业地址", vo.getEnterpriseAddress());
                row.put("数据权限-当前用户所在部门", vo.getDeptId());
                row.put("状态：0-禁用，1-正常", vo.getStatus());
                row.put("删除标志：0-正常，1-删除", vo.getDeleted());
                row.put("创建时间", DateUtil.formatLocalDateTime(vo.getCreateTime()));
                row.put("更新时间", DateUtil.formatLocalDateTime(vo.getUpdateTime()));
                row.put("创建者", vo.getCreateByName());
                row.put("更新者", vo.getUpdateByName());
                rows.add(row);
            }

            // 写入数据
            writer.write(rows, false);

            // 输出到字节数组
            writer.flush(out);
            writer.close();

            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出测试企业Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEnterprise(EnterpriseCreateDTO dto) {
        Enterprise entity = new Enterprise();
        BeanUtils.copyProperties(dto, entity);
        entity.setDeptId(SecurityUtils.getCurrentUserDeptId());
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnterprise(EnterpriseUpdateDTO dto) {
        Enterprise entity = new Enterprise();
        BeanUtils.copyProperties(dto, entity);
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnterprise(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEnterprise(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public EnterpriseVO getEnterpriseById(Long id) {
        Enterprise entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        return convertToVO(entity);
    }

    private EnterpriseVO convertToVO(Enterprise entity){
        EnterpriseVO vo = new EnterpriseVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}

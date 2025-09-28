package ${package.ServiceImpl};

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${package.Parent}.dto.${entity}CreateDTO;
import ${package.Parent}.dto.${entity}UpdateDTO;
import ${package.Parent}.dto.${entity}QueryDTO;
import ${package.Parent}.vo.${entity}VO;
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
 * ${table.comment!}服务实现类
 *
 * @author ${author}
 * @since ${date}
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class ${table.serviceImplName} extends ServiceImpl<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Override
    public IPage<${entity}VO> page${entity}s(${entity}QueryDTO dto) {
        Page<${entity}> page = new Page<>(dto.getCurrent(), dto.getSize());
        Page<${entity}> entityPage = this.page(page, QueryProcessor.createQueryWrapper(dto));
        Page<${entity}VO> pageVO = PageConverter.convert(entityPage, this::convertToVO);
        return pageVO;
    }

    @Override
    public List<${entity}VO> list${entity}s(${entity}QueryDTO dto) {
        QueryWrapper<${entity}> queryWrapper = QueryProcessor.createQueryWrapper(dto);
        List<${entity}> list = this.list(queryWrapper);
        return ListConverter.convert(list, this::convertToVO);
    }

    @Override
    public byte[] export${entity}s(${entity}QueryDTO dto) {
        List<${entity}VO> list = list${entity}s(dto);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 创建Excel写入器
            ExcelWriter writer = ExcelUtil.getWriter(true);
            
            // 设置表头
            Map<String, String> headerMap = new LinkedHashMap<>();
<#list table.fields as field>
  <#if !field.keyFlag>
            headerMap.put("${field.propertyName}", "${field.comment!field.propertyName}");
  </#if>
</#list>
            
            // 写入表头
            writer.writeHeadRow(new ArrayList<>(headerMap.values()));
            
            // 转换数据为Map列表
            List<Map<String, Object>> rows = new ArrayList<>();
            for (${entity}VO vo : list) {
                Map<String, Object> row = new LinkedHashMap<>();
<#list table.fields as field>
  <#if !field.keyFlag>
    <#if field.propertyType == "LocalDateTime">
                row.put("${field.comment!field.propertyName}", DateUtil.formatLocalDateTime(vo.get${field.capitalName}()));
    <#elseif field.propertyType == "LocalDate">
                row.put("${field.comment!field.propertyName}", DateUtil.formatLocalDate(vo.get${field.capitalName}()));
    <#else>
                row.put("${field.comment!field.propertyName}", vo.get${field.capitalName}());
    </#if>
  </#if>
</#list>
                rows.add(row);
            }
            
            // 写入数据
            writer.write(rows, false);
            
            // 输出到字节数组
            writer.flush(out);
            writer.close();
            
            return out.toByteArray();
        } catch (Exception e) {
            log.error("导出${table.comment!}Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create${entity}(${entity}CreateDTO dto) {
        ${entity} entity = new ${entity}();
        BeanUtils.copyProperties(dto, entity);
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update${entity}(${entity}UpdateDTO dto) {
        ${entity} entity = new ${entity}();
        BeanUtils.copyProperties(dto, entity);
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete${entity}(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete${entity}(List<Long> ids) {
        this.removeByIds(ids);
    }

    @Override
    public ${entity}VO get${entity}ById(Long id) {
        ${entity} entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        return convertToVO(entity);
    }

    private ${entity}VO convertToVO(${entity} entity){
        ${entity}VO vo = new ${entity}VO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
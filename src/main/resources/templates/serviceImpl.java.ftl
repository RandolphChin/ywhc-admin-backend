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

import java.util.List;

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
        // TODO: 实现Excel导出逻辑
        // 可以使用EasyExcel或Apache POI
        return new byte[0];
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
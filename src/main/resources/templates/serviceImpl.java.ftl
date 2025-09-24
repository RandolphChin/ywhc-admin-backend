package ${package.ServiceImpl};

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${package.Parent}.dto.${entity}QueryDTO;
import com.ywhc.admin.common.util.PageConverter;
import com.ywhc.admin.common.util.QueryProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ${table.comment!}服务实现类
 *
 * @author ${author}
 * @since ${date}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ${table.serviceImplName} extends ServiceImpl<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    @Override
    public IPage<${entity}> page${entity}s(${entity}QueryDTO queryDTO) {
        Page<${entity}> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<${entity}> queryWrapper = QueryProcessor.createQueryWrapper(queryDTO);
        return this.page(page, queryWrapper);
    }

    @Override
    public List<${entity}> list${entity}s(${entity}QueryDTO queryDTO) {
        LambdaQueryWrapper<${entity}> queryWrapper = QueryProcessor.createQueryWrapper(queryDTO);
        return this.list(queryWrapper);
    }

    @Override
    public byte[] export${entity}s(${entity}QueryDTO queryDTO) {
        List<${entity}> list = list${entity}s(queryDTO);
        // TODO: 实现Excel导出逻辑
        // 可以使用EasyExcel或Apache POI
        return new byte[0];
    }
}
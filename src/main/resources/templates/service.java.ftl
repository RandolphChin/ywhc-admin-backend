package ${package.Service};

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ${package.Entity}.${entity};
import ${package.Parent}.dto.${entity}QueryDTO;

import java.util.List;

/**
 * ${table.comment!}服务接口
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${table.serviceName} extends IService<${entity}> {

    /**
     * 分页查询${table.comment!}
     */
    IPage<${entity}> page${entity}s(${entity}QueryDTO queryDTO);

    /**
     * 查询所有${table.comment!}列表
     */
    List<${entity}> list${entity}s(${entity}QueryDTO queryDTO);

    /**
     * 导出${table.comment!}
     */
    byte[] export${entity}s(${entity}QueryDTO queryDTO);
}
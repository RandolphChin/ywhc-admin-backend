package ${package.Service};

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ${package.Entity}.${entity};
import ${package.Parent}.dto.${entity}CreateDTO;
import ${package.Parent}.dto.${entity}UpdateDTO;
import ${package.Parent}.dto.${entity}QueryDTO;
import ${package.Parent}.vo.${entity}VO;
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
    IPage<${entity}VO> page${entity}s(${entity}QueryDTO dto);

    /**
     * 查询所有${table.comment!}列表
     */
    List<${entity}VO> list${entity}s(${entity}QueryDTO dto);

    /**
     * 导出${table.comment!}
     */
    byte[] export${entity}s(${entity}QueryDTO dto);

    /**
     * 创建${table.comment!}
     */
    Long create${entity}(${entity}CreateDTO dto);

    /**
     * 更新${table.comment!}
     */
    void update${entity}(${entity}UpdateDTO dto);

    /**
     * 删除${table.comment!}
     */
    void delete${entity}(Long id);

    /**
     * 批量删除${table.comment!}
     */
    void delete${entity}(List<Long> ids);

    /**
     * 根据ID获取${table.comment!}详情
     */
    ${entity}VO get${entity}ById(Long id);
}
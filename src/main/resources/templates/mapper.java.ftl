package ${package.Mapper};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ${package.Entity}.${entity};
import org.apache.ibatis.annotations.Mapper;

/**
 * ${table.comment!}Mapper接口
 *
 * @author ${author}
 * @since ${date}
 */
@Mapper
public interface ${table.mapperName} extends BaseMapper<${entity}> {

}
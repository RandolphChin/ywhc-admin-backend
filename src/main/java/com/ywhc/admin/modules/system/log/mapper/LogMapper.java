package com.ywhc.admin.modules.system.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywhc.admin.modules.system.log.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志Mapper接口
 * 
 * @author YWHC Team
 * @since 2024-01-01
 */
@Mapper
public interface LogMapper extends BaseMapper<SysLog> {

}

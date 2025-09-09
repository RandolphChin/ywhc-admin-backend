package com.ywhc.admin.modules.system.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.log.entity.SysLog;
import com.ywhc.admin.modules.system.log.dto.LogQueryDTO;
import com.ywhc.admin.modules.system.log.vo.LogVO;

/**
 * 日志服务接口
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface LogService extends IService<SysLog> {

    /**
     * 分页查询日志
     */
    IPage<LogVO> pageLogs(LogQueryDTO queryDTO);

    /**
     * 保存操作日志
     */
    void saveLog(SysLog log);

    /**
     * 清空日志
     */
    void clearLogs();
}

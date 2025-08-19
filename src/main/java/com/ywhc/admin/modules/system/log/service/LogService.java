package com.ywhc.admin.modules.system.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywhc.admin.modules.system.log.entity.SysLog;

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
    IPage<SysLog> pageLogs(Long current, Long size, String module, String operationDesc, Integer status);

    /**
     * 保存操作日志
     */
    void saveLog(SysLog log);

    /**
     * 清空日志
     */
    void clearLogs();
}

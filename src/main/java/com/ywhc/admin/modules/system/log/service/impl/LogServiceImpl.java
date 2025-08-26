package com.ywhc.admin.modules.system.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.modules.system.log.entity.SysLog;
import com.ywhc.admin.modules.system.log.dto.LogQueryDTO;
import com.ywhc.admin.modules.system.log.mapper.LogMapper;
import com.ywhc.admin.modules.system.log.service.LogService;
import com.ywhc.admin.common.util.QueryProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 日志服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl extends ServiceImpl<LogMapper, SysLog> implements LogService {

    @Override
    public IPage<SysLog> pageLogs(LogQueryDTO queryDTO) {
        Page<SysLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        return this.page(page, QueryProcessor.createQueryWrapper(queryDTO));
    }

    @Override
    public void saveLog(SysLog logs) {
        try {
            this.save(logs);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage());
        }
    }

    @Override
    public void clearLogs() {
        this.remove(new LambdaQueryWrapper<>());
    }
}

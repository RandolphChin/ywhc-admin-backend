package com.ywhc.admin.modules.system.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywhc.admin.modules.system.log.entity.SysLog;
import com.ywhc.admin.modules.system.log.mapper.LogMapper;
import com.ywhc.admin.modules.system.log.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public IPage<SysLog> pageLogs(Long current, Long size, String module, Integer operationType, Integer status) {
        Page<SysLog> page = new Page<>(current, size);
        
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(module), SysLog::getModule, module)
               .eq(operationType != null, SysLog::getOperationType, operationType)
               .eq(status != null, SysLog::getStatus, status)
               .orderByDesc(SysLog::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public void saveLog(SysLog log) {
        try {
            this.save(log);
        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage());
        }
    }

    @Override
    public void clearLogs() {
        this.remove(new LambdaQueryWrapper<>());
    }
}

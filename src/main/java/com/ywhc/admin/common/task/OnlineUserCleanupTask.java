package com.ywhc.admin.common.task;

import com.ywhc.admin.modules.monitor.online.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 在线用户清理定时任务
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OnlineUserCleanupTask {

    private final OnlineUserService onlineUserService;

    /**
     * 每小时统计在线用户数量
     * cron表达式：0 0 * * * ? 表示每小时整点执行一次
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void logOnlineUserCount() {
        try {
            long count = onlineUserService.getOnlineUserCount();
            log.info("当前在线用户数量: {}", count);
        } catch (Exception e) {
            log.error("统计在线用户数量失败: {}", e.getMessage(), e);
        }
    }
}

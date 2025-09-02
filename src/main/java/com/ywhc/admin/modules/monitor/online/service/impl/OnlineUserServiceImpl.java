package com.ywhc.admin.modules.monitor.online.service.impl;

import com.ywhc.admin.modules.monitor.online.dto.OnlineUserQueryDTO;
import com.ywhc.admin.modules.monitor.online.entity.OnlineUser;
import com.ywhc.admin.modules.monitor.online.service.OnlineUserService;
import com.ywhc.admin.modules.monitor.online.vo.OnlineUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 在线用户服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineUserServiceImpl implements OnlineUserService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String ONLINE_USER_KEY = "online_user:";
    private static final String TOKEN_BLACKLIST_KEY = "token_blacklist:";
    private static final String USER_TOKEN_KEY = "user_tokens:";

    @Override
    public void saveOnlineUser(OnlineUser onlineUser) {
        try {
            String key = ONLINE_USER_KEY + onlineUser.getAccessToken();

            // 保存在线用户信息，设置过期时间
            redisTemplate.opsForValue().set(key, onlineUser,
                Duration.between(LocalDateTime.now(), onlineUser.getExpireTime()));

            // 保存用户ID与Token的映射关系，用于根据用户ID查找所有Token
            String userTokenKey = USER_TOKEN_KEY + onlineUser.getUserId();
            redisTemplate.opsForSet().add(userTokenKey, onlineUser.getAccessToken());
            redisTemplate.expire(userTokenKey,
                Duration.between(LocalDateTime.now(), onlineUser.getExpireTime()));

            log.debug("保存在线用户信息成功: userId={}, token={}",
                onlineUser.getUserId(), maskToken(onlineUser.getAccessToken()));
        } catch (Exception e) {
            log.error("保存在线用户信息失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public OnlineUser getOnlineUserByToken(String token) {
        try {
            String key = ONLINE_USER_KEY + token;
            return (OnlineUser) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取在线用户信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<OnlineUser> getOnlineUsersByUserId(Long userId) {
        try {
            String userTokenKey = USER_TOKEN_KEY + userId;
            Set<Object> tokens = redisTemplate.opsForSet().members(userTokenKey);

            if (tokens == null || tokens.isEmpty()) {
                return new ArrayList<>();
            }

            List<OnlineUser> onlineUsers = new ArrayList<>();
            for (Object tokenObj : tokens) {
                String token = (String) tokenObj;
                OnlineUser onlineUser = getOnlineUserByToken(token);
                if (onlineUser != null) {
                    onlineUsers.add(onlineUser);
                } else {
                    // 清理无效的Token映射
                    redisTemplate.opsForSet().remove(userTokenKey, token);
                }
            }

            return onlineUsers;
        } catch (Exception e) {
            log.error("根据用户ID获取在线用户信息失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<OnlineUserVO> getOnlineUsers(OnlineUserQueryDTO queryDTO) {
        try {
            Set<String> keys = redisTemplate.keys(ONLINE_USER_KEY + "*");
            if (keys == null || keys.isEmpty()) {
                return new ArrayList<>();
            }

            List<OnlineUser> onlineUsers = new ArrayList<>();
            for (String key : keys) {
                OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(key);
                if (onlineUser != null && matchesQuery(onlineUser, queryDTO)) {
                    onlineUsers.add(onlineUser);
                }
            }

            // 转换为VO并排序
            return onlineUsers.stream()
                .map(this::convertToVO)
                .sorted((a, b) -> b.getLoginTime().compareTo(a.getLoginTime()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取在线用户列表失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public void updateLastAccessTime(String token) {
        try {
            OnlineUser onlineUser = getOnlineUserByToken(token);
            if (onlineUser != null) {
                onlineUser.setLastAccessTime(LocalDateTime.now());
                saveOnlineUser(onlineUser);
            }
        } catch (Exception e) {
            log.error("更新用户最后活动时间失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void forceLogout(String token) {
        try {
            OnlineUser onlineUser = getOnlineUserByToken(token);
            if (onlineUser != null) {
                // 将Token加入黑名单
                long expireTime = Duration.between(LocalDateTime.now(), onlineUser.getExpireTime()).getSeconds();
                addTokenToBlacklist(token, expireTime);

                // 删除在线用户信息
                removeOnlineUserByToken(token);

                log.info("强制用户下线成功: userId={}, token={}",
                    onlineUser.getUserId(), maskToken(token));
            }
        } catch (Exception e) {
            log.error("强制用户下线失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void forceLogoutByUserId(Long userId) {
        try {
            List<OnlineUser> onlineUsers = getOnlineUsersByUserId(userId);
            for (OnlineUser onlineUser : onlineUsers) {
                forceLogout(onlineUser.getAccessToken());
            }
            log.info("强制用户所有会话下线成功: userId={}", userId);
        } catch (Exception e) {
            log.error("强制用户所有会话下线失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    @Override
    public void removeExpiredUsers() {
        try {
            Set<String> keys = redisTemplate.keys(ONLINE_USER_KEY + "*");
            if (keys == null || keys.isEmpty()) {
                return;
            }

            int removedCount = 0;
            for (String key : keys) {
                OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(key);
                if (onlineUser == null || onlineUser.getExpireTime().isBefore(LocalDateTime.now())) {
                    redisTemplate.delete(key);
                    removedCount++;
                }
            }

            if (removedCount > 0) {
                log.info("清理过期在线用户: {} 个", removedCount);
            }
        } catch (Exception e) {
            log.error("清理过期在线用户失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void removeOnlineUserByToken(String token) {
        try {
            OnlineUser onlineUser = getOnlineUserByToken(token);
            if (onlineUser != null) {
                // 删除在线用户信息
                String key = ONLINE_USER_KEY + token;
                redisTemplate.delete(key);

                // 删除用户Token映射
                String userTokenKey = USER_TOKEN_KEY + onlineUser.getUserId();
                redisTemplate.opsForSet().remove(userTokenKey, token);

                log.debug("删除在线用户信息成功: userId={}, token={}",
                    onlineUser.getUserId(), maskToken(token));
            }
        } catch (Exception e) {
            log.error("删除在线用户信息失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public long getOnlineUserCount() {
        try {
            Set<String> keys = redisTemplate.keys(ONLINE_USER_KEY + "*");
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            log.error("获取在线用户总数失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            String key = TOKEN_BLACKLIST_KEY + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查Token黑名单状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void addTokenToBlacklist(String token, long expireTime) {
        try {
            String key = TOKEN_BLACKLIST_KEY + token;
            redisTemplate.opsForValue().set(key, "blacklisted", expireTime, TimeUnit.SECONDS);
            log.debug("Token已加入黑名单: {}", maskToken(token));
        } catch (Exception e) {
            log.error("将Token加入黑名单失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public String[] parseUserAgent(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return new String[]{"Unknown", "Unknown"};
        }

        String browser = parseBrowser(userAgent);
        String os = parseOS(userAgent);

        return new String[]{browser, os};
    }

    @Override
    public String getLocationByIp(String ipAddress) {
        // TODO: 集成IP地址库或第三方API获取地理位置
        // 这里暂时返回默认值，可以后续集成如：
        // 1. 使用离线IP数据库（如MaxMind GeoLite2）
        // 2. 调用第三方API（如百度地图、高德地图等）
        if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            return "本地";
        }
        return "未知";
    }

    /**
     * 检查在线用户是否匹配查询条件
     */
    private boolean matchesQuery(OnlineUser onlineUser, OnlineUserQueryDTO queryDTO) {
        if (queryDTO == null) {
            return true;
        }

        // 用户名精确匹配
        if (StringUtils.hasText(queryDTO.getUsername())
            && !queryDTO.getUsername().equals(onlineUser.getUsername())) {
            return false;
        }

        // 用户名模糊匹配
        if (StringUtils.hasText(queryDTO.getUsernameLike())
            && (onlineUser.getUsername() == null ||
                !onlineUser.getUsername().contains(queryDTO.getUsernameLike()))) {
            return false;
        }

        // 昵称模糊匹配
        if (StringUtils.hasText(queryDTO.getNicknameLike())
            && (onlineUser.getNickname() == null ||
                !onlineUser.getNickname().contains(queryDTO.getNicknameLike()))) {
            return false;
        }

        // IP地址匹配
        if (StringUtils.hasText(queryDTO.getIpAddress())
            && !queryDTO.getIpAddress().equals(onlineUser.getIpAddress())) {
            return false;
        }

        // 状态匹配
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().equals(onlineUser.getStatus())) {
            return false;
        }

        // 设备类型匹配
        if (queryDTO.getDeviceType() != null && !queryDTO.getDeviceType().equals(onlineUser.getDeviceType())) {
            return false;
        }

        return true;
    }

    /**
     * 转换为VO
     */
    private OnlineUserVO convertToVO(OnlineUser onlineUser) {
        OnlineUserVO vo = new OnlineUserVO();
        BeanUtils.copyProperties(onlineUser, vo);

        // 设置状态描述
        vo.setStatusDesc(onlineUser.getStatus() == 1 ? "在线" : "离线");

        // 设置设备类型描述
        vo.setDeviceTypeDesc(onlineUser.getDeviceType() == 1 ? "PC" : "移动端");

        // 计算在线时长（分钟）
        if (onlineUser.getLoginTime() != null) {
            LocalDateTime endTime = onlineUser.getLastAccessTime() != null ?
                onlineUser.getLastAccessTime() : LocalDateTime.now();
            long duration = Duration.between(onlineUser.getLoginTime(), endTime).toMinutes();
            vo.setOnlineDuration(Math.max(0, duration));
        }

        // 脱敏Token
        vo.setAccessToken(maskToken(onlineUser.getAccessToken()));

        return vo;
    }

    /**
     * 解析浏览器信息
     */
    private String parseBrowser(String userAgent) {
        if (userAgent.contains("Edg/")) {
            return "Microsoft Edge";
        } else if (userAgent.contains("Chrome/")) {
            return "Google Chrome";
        } else if (userAgent.contains("Firefox/")) {
            return "Mozilla Firefox";
        } else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) {
            return "Safari";
        } else if (userAgent.contains("Opera/") || userAgent.contains("OPR/")) {
            return "Opera";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident/")) {
            return "Internet Explorer";
        }
        return "Unknown";
    }

    /**
     * 解析操作系统信息
     */
    private String parseOS(String userAgent) {
        if (userAgent.contains("Windows NT 10.0")) {
            return "Windows 10";
        } else if (userAgent.contains("Windows NT 6.3")) {
            return "Windows 8.1";
        } else if (userAgent.contains("Windows NT 6.2")) {
            return "Windows 8";
        } else if (userAgent.contains("Windows NT 6.1")) {
            return "Windows 7";
        } else if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac OS X")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        return "Unknown";
    }

    /**
     * Token脱敏处理
     */
    private String maskToken(String token) {
        if (!StringUtils.hasText(token) || token.length() <= 10) {
            return token;
        }
        return token.substring(0, 6) + "****" + token.substring(token.length() - 4);
    }
}

package com.ywhc.admin.modules.monitor.online.service;

import com.ywhc.admin.modules.monitor.online.dto.OnlineUserQueryDTO;
import com.ywhc.admin.modules.monitor.online.entity.OnlineUser;
import com.ywhc.admin.modules.monitor.online.vo.OnlineUserVO;

import java.util.List;

/**
 * 在线用户服务接口
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface OnlineUserService {

    /**
     * 保存在线用户信息到Redis
     *
     * @param onlineUser 在线用户信息
     */
    void saveOnlineUser(OnlineUser onlineUser);

    /**
     * 根据Token获取在线用户信息
     *
     * @param token 访问Token
     * @return 在线用户信息
     */
    OnlineUser getOnlineUserByToken(String token);

    /**
     * 根据用户ID获取在线用户信息
     *
     * @param userId 用户ID
     * @return 在线用户信息列表
     */
    List<OnlineUser> getOnlineUsersByUserId(Long userId);

    /**
     * 获取所有在线用户列表
     *
     * @param queryDTO 查询条件
     * @return 在线用户列表
     */
    List<OnlineUserVO> getOnlineUsers(OnlineUserQueryDTO queryDTO);

    /**
     * 更新用户最后活动时间
     *
     * @param token 访问Token
     */
    void updateLastAccessTime(String token);

    /**
     * 强制用户下线
     *
     * @param token 访问Token
     */
    void forceLogout(String token);

    /**
     * 根据用户ID强制所有会话下线
     *
     * @param userId 用户ID
     */
    void forceLogoutByUserId(Long userId);

    /**
     * 删除过期的在线用户
     */
    void removeExpiredUsers();

    /**
     * 根据Token删除在线用户
     *
     * @param token 访问Token
     */
    void removeOnlineUserByToken(String token);

    /**
     * 获取在线用户总数
     *
     * @return 在线用户总数
     */
    long getOnlineUserCount();

    /**
     * 检查Token是否在黑名单中
     *
     * @param token 访问Token
     * @return 是否在黑名单中
     */
    boolean isTokenBlacklisted(String token);

    /**
     * 将Token加入黑名单
     *
     * @param token 访问Token
     * @param expireTime Token过期时间（秒）
     */
    void addTokenToBlacklist(String token, long expireTime);

    /**
     * 解析User-Agent获取浏览器和操作系统信息
     *
     * @param userAgent 用户代理字符串
     * @return 包含浏览器和操作系统信息的数组 [browser, os]
     */
    String[] parseUserAgent(String userAgent);

    /**
     * 根据IP地址获取地理位置信息
     *
     * @param ipAddress IP地址
     * @return 地理位置信息
     */
    String getLocationByIp(String ipAddress);
}

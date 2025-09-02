# 在线用户管理功能说明

## 概述

在线用户管理功能基于Redis实现，用于实时监控和管理系统中的在线用户。当用户登录时，系统会将用户信息和Token保存到Redis中；当用户登出或Token过期时，会自动清理相关数据。

## 功能特性

### 核心功能
- **实时监控**: 实时显示当前在线的用户列表
- **用户信息**: 展示用户名、昵称、登录IP、登录时间、浏览器、操作系统等详细信息
- **强制下线**: 管理员可以强制指定用户下线
- **Token黑名单**: 支持Token黑名单机制，防止已下线用户继续访问
- **自动清理**: 定时清理过期的在线用户数据
- **多端支持**: 支持PC端和移动端设备类型识别

### 技术特点
- **Redis存储**: 使用Redis作为存储介质，支持高并发访问
- **自动过期**: 利用Redis的TTL机制自动清理过期数据
- **实时更新**: 用户活动时自动更新最后访问时间
- **安全机制**: 集成JWT Token黑名单，增强安全性

## 核心组件

### 1. 实体类 (OnlineUser)
```java
@Data
public class OnlineUser {
    private Long userId;           // 用户ID
    private String username;       // 用户名
    private String nickname;       // 昵称
    private String accessToken;    // 访问Token
    private String refreshToken;   // 刷新Token
    private String ipAddress;      // 登录IP
    private String location;       // 登录地点
    private String browser;        // 浏览器类型
    private String os;            // 操作系统
    private LocalDateTime loginTime;        // 登录时间
    private LocalDateTime lastAccessTime;   // 最后访问时间
    private LocalDateTime expireTime;       // 过期时间
    private Integer status;        // 状态：1-在线，0-离线
    private Integer deviceType;    // 设备类型：1-PC，2-移动端
}
```

### 2. 服务接口 (OnlineUserService)
主要方法：
- `saveOnlineUser()`: 保存在线用户信息到Redis
- `getOnlineUsers()`: 获取在线用户列表
- `forceLogout()`: 强制用户下线
- `updateLastAccessTime()`: 更新最后活动时间
- `removeExpiredUsers()`: 清理过期用户
- `isTokenBlacklisted()`: 检查Token是否在黑名单

### 3. 控制器 (OnlineUserController)
提供RESTful API接口：
- `GET /monitor/online/list`: 获取在线用户列表
- `GET /monitor/online/count`: 获取在线用户总数
- `DELETE /monitor/online/force-logout`: 强制用户下线
- `DELETE /monitor/online/force-logout-user/{userId}`: 强制用户所有会话下线

## Redis数据结构

### Key命名规则
```
online_user:{token}      # 存储在线用户信息
user_tokens:{userId}     # 存储用户ID与Token的映射关系
token_blacklist:{token}  # Token黑名单
```

### 数据生命周期
- **在线用户数据**: 与Token过期时间一致（默认24小时）
- **Token黑名单**: 与原Token的剩余有效期一致
- **用户Token映射**: 与Token过期时间一致

## 集成流程

### 1. 用户登录流程
```
1. 用户提交登录信息 (AuthController.login)
2. 验证用户凭据 (AuthServiceImpl.login)
3. 生成访问Token和刷新Token (JwtUtils)
4. 解析用户设备信息 (parseUserAgent)
5. 保存在线用户信息到Redis (OnlineUserService.saveOnlineUser)
6. 返回登录成功响应
```

### 2. 用户访问流程
```
1. 用户发起请求携带Token
2. JWT过滤器拦截请求 (JwtAuthenticationFilter)
3. 检查Token是否在黑名单 (OnlineUserService.isTokenBlacklisted)
4. 验证Token有效性 (JwtUtils.validateToken)
5. 更新最后活动时间 (OnlineUserService.updateLastAccessTime)
6. 继续处理请求
```

### 3. 用户登出流程
```
1. 用户请求登出 (AuthController.logout)
2. 获取当前用户Token (AuthServiceImpl.logout)
3. 从Redis删除在线用户信息 (OnlineUserService.removeOnlineUserByToken)
4. 将Token加入黑名单 (OnlineUserService.addTokenToBlacklist)
5. 清除安全上下文
```

## 权限配置

需要在菜单管理中配置以下权限：

### 菜单权限
- `monitor:online:list`: 查看在线用户列表
- `monitor:online:forceLogout`: 强制用户下线
- `monitor:online:clean`: 清理过期用户

### 菜单结构示例
```sql
-- 系统监控目录
INSERT INTO sys_menu VALUES (6, 0, '系统监控', 0, '/monitor', NULL, 'monitor:manage', 'monitor', 2, 0, 1, 1, 1, '系统监控目录');

-- 在线用户菜单
INSERT INTO sys_menu VALUES (7, 6, '在线用户', 1, '/monitor/online', 'monitor/online/index', 'monitor:online:list', 'people_outline', 1, 0, 1, 1, 1, '在线用户菜单');

-- 按钮权限
INSERT INTO sys_menu VALUES (41, 7, '强制下线', 2, NULL, NULL, 'monitor:online:forceLogout', NULL, 1, 0, 1, 1, 1, '强制下线按钮');
```

## 定时任务

系统提供了自动清理机制：

### OnlineUserCleanupTask
- **清理过期用户**: 每10分钟执行一次 `@Scheduled(cron = "0 */10 * * * ?")`
- **统计在线用户**: 每小时执行一次 `@Scheduled(cron = "0 0 * * * ?")`

## 配置说明

### Redis配置
确保application.yml中配置了Redis连接信息：
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 
      database: 0
```

### JWT配置
确保JWT配置正确：
```yaml
jwt:
  secret: your-secret-key
  expiration: 86400000    # 24小时
  refresh-expiration: 604800000  # 7天
  token-prefix: "Bearer "
```

## 安全考虑

### Token安全
1. **黑名单机制**: 用户登出后Token立即失效
2. **过期清理**: 自动清理过期的Token和用户信息
3. **Token脱敏**: 日志和前端显示时对Token进行脱敏处理

### 数据安全
1. **敏感信息**: 不存储用户密码等敏感信息
2. **访问控制**: 严格的权限控制，只有管理员可以查看和操作
3. **审计日志**: 关键操作会记录到系统日志

## 扩展功能

### 地理位置
当前返回默认值，可扩展集成：
- MaxMind GeoLite2 离线数据库
- 百度地图、高德地图等第三方API

### User-Agent解析
当前支持主流浏览器和操作系统识别，可扩展：
- 更详细的设备信息识别
- 移动设备型号识别
- 爬虫和机器人识别

### 监控告警
可扩展添加：
- 异常登录告警（异地登录、频繁登录等）
- 在线用户数量告警
- 系统负载监控

## 常见问题

### Q1: 用户多端登录如何处理？
A: 系统支持同一用户多端登录，每个设备会生成独立的Token和在线用户记录。

### Q2: 如何强制用户单点登录？
A: 在登录时可以先调用`forceLogoutByUserId(userId)`强制该用户的所有其他会话下线。

### Q3: Redis数据量过大怎么办？
A: 
- 调整Token过期时间
- 增加清理任务频率
- 使用Redis集群
- 设置Redis内存淘汰策略

### Q4: 如何查看Redis中的数据？
A: 可以使用Redis客户端工具查看：
```bash
# 查看所有在线用户Key
KEYS online_user:*

# 查看特定用户信息
GET online_user:{token}

# 查看用户Token映射
SMEMBERS user_tokens:{userId}

# 查看Token黑名单
KEYS token_blacklist:*
```

## 注意事项

1. **Redis依赖**: 确保Redis服务正常运行，否则在线用户功能不可用
2. **性能影响**: 每次请求都会更新Redis，在高并发场景下注意性能
3. **数据一致性**: Redis数据可能与实际用户状态存在短暂不一致
4. **定时任务**: 确保定时任务正常运行，避免数据堆积
5. **内存使用**: 监控Redis内存使用情况，避免OOM

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 支持基本的在线用户管理功能
- 集成JWT Token黑名单机制
- 提供RESTful API接口
- 支持定时清理过期数据
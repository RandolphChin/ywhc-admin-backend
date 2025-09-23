package com.ywhc.admin.modules.auth.service.impl;

import com.ywhc.admin.common.result.ResultCode;
import com.ywhc.admin.common.security.service.SecurityUser;
import com.ywhc.admin.common.utils.JwtUtils;
import com.ywhc.admin.modules.auth.dto.ChangePasswordDTO;
import com.ywhc.admin.modules.auth.dto.LoginDTO;
import com.ywhc.admin.modules.auth.service.AuthService;
import com.ywhc.admin.modules.auth.vo.LoginVO;
import com.ywhc.admin.modules.auth.vo.UserInfoVO;
import com.ywhc.admin.modules.monitor.online.entity.OnlineUser;
import com.ywhc.admin.modules.monitor.online.service.OnlineUserService;
import com.ywhc.admin.modules.system.user.entity.SysUser;
import com.ywhc.admin.modules.system.user.service.UserService;
import com.ywhc.admin.modules.system.dept.service.SysDeptService;
import com.ywhc.admin.modules.captcha.service.SlideCaptchaService;
import com.ywhc.admin.common.security.service.RSAKeyService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 认证服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final OnlineUserService onlineUserService;
    private final SysDeptService sysDeptService;
    private final SlideCaptchaService slideCaptchaService;
    private final PasswordEncoder passwordEncoder;
    private final RSAKeyService rsaKeyService;

    @Override
    public LoginVO login(
        LoginDTO loginDTO,
        String clientIp,
        HttpServletRequest request
    ) {
        // 验证滑块验证码
        if (StringUtils.hasText(loginDTO.getCaptchaToken())) {
            boolean isValidCaptcha = slideCaptchaService.validateToken(loginDTO.getCaptchaToken());
            if (!isValidCaptcha) {
                throw new RuntimeException("验证码已过期或无效，请重新验证");
            }
        } else {
            throw new RuntimeException("请完成滑块验证");
        }
        
        // 处理密码解密
        String actualPassword = loginDTO.getPassword();
        if (Boolean.TRUE.equals(loginDTO.getEncrypted())) {
            try {
                // 解密密码
                String decryptedData = rsaKeyService.decrypt(loginDTO.getPassword());
                
                // 解析解密后的数据：格式为 "password|timestamp"
                String[] parts = decryptedData.split("\\|");
                if (parts.length != 2) {
                    throw new RuntimeException("密码格式错误");
                }
                
                actualPassword = parts[0];
                long timestamp = Long.parseLong(parts[1]);
                
                // 验证时间戳（防重放攻击，允许5分钟内的请求）
                if (!rsaKeyService.isValidTimestamp(timestamp, 300)) {
                    throw new RuntimeException("请求已过期，请重新登录");
                }
                
                log.debug("密码解密成功，用户: {}", loginDTO.getUsername());
            } catch (NumberFormatException e) {
                throw new RuntimeException("密码格式错误", e);
            } catch (Exception e) {
                log.error("密码解密失败: {}", e.getMessage());
                throw new RuntimeException("密码解密失败，请刷新页面重试", e);
            }
        }
        
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                actualPassword
            )
        );

        SecurityUser securityUser =
            (SecurityUser) authentication.getPrincipal();
        SysUser user = securityUser.getUser();

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException(ResultCode.USER_DISABLED.getMessage());
        }

        // 生成Token
        String accessToken = jwtUtils.generateToken(
            user.getUsername(),
            user.getId()
        );
        String refreshToken = jwtUtils.generateRefreshToken(
            user.getUsername(),
            user.getId()
        );

        // 保存在线用户信息到Redis
        saveOnlineUserToRedis(
            user,
            accessToken,
            refreshToken,
            clientIp,
            request
        );

        // 更新用户登录信息
        userService.updateLoginInfo(user.getId(), clientIp);

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(jwtUtils.getExpiration()); // 24小时

        // 用户信息
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setUserId(user.getId());
        loginVO.setUserInfo(userInfo);

        log.info("用户 {} 登录成功，IP: {}", user.getUsername(), clientIp);
        return loginVO;
    }

    @Override
    public void logout(HttpServletRequest request) {
        // 获取当前用户
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        if (
            authentication != null &&
            authentication.getPrincipal() instanceof SecurityUser securityUser
        ) {
            log.info("用户 {} 登出成功", securityUser.getUsername());

            // 从请求头获取Token
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token)) {
                // 从Redis中删除在线用户信息
                onlineUserService.removeOnlineUserByToken(token);

                // 将Token加入黑名单
                OnlineUser onlineUser = onlineUserService.getOnlineUserByToken(
                    token
                );
                if (onlineUser != null) {
                    long expireTime = java.time.Duration.between(
                        LocalDateTime.now(),
                        onlineUser.getExpireTime()
                    ).getSeconds();
                    if (expireTime > 0) {
                        onlineUserService.addTokenToBlacklist(
                            token,
                            expireTime
                        );
                    }
                }
            }
        }

        // 清除安全上下文
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        if (
            authentication == null ||
            !(authentication.getPrincipal() instanceof
                SecurityUser securityUser)
        ) {
            throw new RuntimeException(ResultCode.UNAUTHORIZED.getMessage());
        }

        SysUser user = securityUser.getUser();

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        userInfoVO.setUserId(user.getId());

        // 获取用户角色和权限
        Set<String> roles = userService.getUserRoles(user.getId());
        Set<String> permissions = userService.getUserPermissions(user.getId());

        userInfoVO.setRoles(roles);
        userInfoVO.setPermissions(permissions);

        // 获取用户部门信息
        if (user.getDeptId() != null) {
            userInfoVO.setDeptId(user.getDeptId());
            try {
                var dept = sysDeptService.getById(user.getDeptId());
                if (dept != null) {
                    userInfoVO.setDeptName(dept.getDeptName());
                }
            } catch (Exception e) {
                log.warn("获取用户部门信息失败: {}", e.getMessage());
            }
        }

        // 获取用户数据权限范围
        try {
            Set<Long> dataScope = sysDeptService.getDataScope(user.getId());
            userInfoVO.setDataScope(dataScope.stream().toList());
        } catch (Exception e) {
            log.warn("获取用户数据权限失败: {}", e.getMessage());
        }

        // TODO: 获取用户菜单树

        return userInfoVO;
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        try {
            // 验证刷新Token
            if (!jwtUtils.isValidToken(refreshToken)) {
                throw new RuntimeException(
                    ResultCode.REFRESH_TOKEN_INVALID.getMessage()
                );
            }

            // 从Token中获取用户信息
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            Long userId = jwtUtils.getUserIdFromToken(refreshToken);

            // 生成新的访问Token
            String newAccessToken = jwtUtils.generateToken(username, userId);
            String newRefreshToken = jwtUtils.generateRefreshToken(
                username,
                userId
            );

            // 更新Redis中的在线用户信息
            OnlineUser oldOnlineUser = onlineUserService.getOnlineUserByToken(
                refreshToken
            );
            if (oldOnlineUser != null) {
                // 删除旧的Token信息
                onlineUserService.removeOnlineUserByToken(refreshToken);

                // 保存新的Token信息
                oldOnlineUser.setAccessToken(newAccessToken);
                oldOnlineUser.setRefreshToken(newRefreshToken);
                oldOnlineUser.setLastAccessTime(LocalDateTime.now());
                oldOnlineUser.setExpireTime(
                    LocalDateTime.now().plusSeconds(86400)
                );
                onlineUserService.saveOnlineUser(oldOnlineUser);
            }

            // 构建响应
            LoginVO loginVO = new LoginVO();
            loginVO.setAccessToken(newAccessToken);
            loginVO.setRefreshToken(newRefreshToken);
            loginVO.setExpiresIn(86400L);

            return loginVO;
        } catch (Exception e) {
            log.error("Token刷新失败: {}", e.getMessage());
            throw new RuntimeException(
                ResultCode.REFRESH_TOKEN_INVALID.getMessage()
            );
        }
    }

    /**
     * 保存在线用户信息到Redis
     */
    private void saveOnlineUserToRedis(
        SysUser user,
        String accessToken,
        String refreshToken,
        String clientIp,
        HttpServletRequest request
    ) {
        try {
            OnlineUser onlineUser = new OnlineUser();
            onlineUser.setUserId(user.getId());
            onlineUser.setDeptId(user.getDeptId());
            onlineUser.setDeptName(user.getDeptName());
            onlineUser.setUsername(user.getUsername());
            onlineUser.setNickname(user.getNickname());
            onlineUser.setAccessToken(accessToken);
            onlineUser.setRefreshToken(refreshToken);
            onlineUser.setIpAddress(clientIp);

            // 解析User-Agent获取浏览器和操作系统信息
            if (request != null) {
                String userAgent = request.getHeader("User-Agent");
                onlineUser.setUserAgent(userAgent);

                String[] browserAndOS = onlineUserService.parseUserAgent(
                    userAgent
                );
                onlineUser.setBrowser(browserAndOS[0]);
                onlineUser.setOs(browserAndOS[1]);

                // 判断设备类型（简单判断，可以根据需要优化）
                if (
                    userAgent != null &&
                    (userAgent.contains("Mobile") ||
                        userAgent.contains("Android") ||
                        userAgent.contains("iPhone"))
                ) {
                    onlineUser.setDeviceType(2); // 移动端
                } else {
                    onlineUser.setDeviceType(1); // PC
                }
            } else {
                onlineUser.setDeviceType(1); // 默认PC
                onlineUser.setBrowser("Unknown");
                onlineUser.setOs("Unknown");
            }

            // 获取地理位置信息
            onlineUser.setLocation(onlineUserService.getLocationByIp(clientIp));

            // 设置时间信息
            LocalDateTime now = LocalDateTime.now();
            onlineUser.setLoginTime(now);
            onlineUser.setLastAccessTime(now);
            onlineUser.setExpireTime(now.plusSeconds(86400)); // 24小时后过期

            // 设置状态
            onlineUser.setStatus(1); // 在线

            // 保存到Redis
            onlineUserService.saveOnlineUser(onlineUser);

            log.debug(
                "在线用户信息已保存到Redis: userId={}, username={}",
                user.getId(),
                user.getUsername()
            );
        } catch (Exception e) {
            log.error("保存在线用户信息到Redis失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String tokenPrefix = jwtUtils.getTokenPrefix();
        if (
            StringUtils.hasText(bearerToken) &&
            bearerToken.startsWith(tokenPrefix)
        ) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new RuntimeException(ResultCode.UNAUTHORIZED.getMessage());
        }

        SysUser user = securityUser.getUser();
        
        // 处理密码解密
        String actualOldPassword = changePasswordDTO.getOldPassword();
        String actualNewPassword = changePasswordDTO.getNewPassword();
        
        if (Boolean.TRUE.equals(changePasswordDTO.getEncrypted())) {
            try {
                // 解密原密码
                String decryptedOldData = rsaKeyService.decrypt(changePasswordDTO.getOldPassword());
                String[] oldParts = decryptedOldData.split("\\|");
                if (oldParts.length != 2) {
                    throw new RuntimeException("原密码格式错误");
                }
                actualOldPassword = oldParts[0];
                long oldTimestamp = Long.parseLong(oldParts[1]);
                
                // 解密新密码
                String decryptedNewData = rsaKeyService.decrypt(changePasswordDTO.getNewPassword());
                String[] newParts = decryptedNewData.split("\\|");
                if (newParts.length != 2) {
                    throw new RuntimeException("新密码格式错误");
                }
                actualNewPassword = newParts[0];
                long newTimestamp = Long.parseLong(newParts[1]);
                
                // 验证时间戳（防重放攻击，允许5分钟内的请求）
                if (!rsaKeyService.isValidTimestamp(oldTimestamp, 300) || 
                    !rsaKeyService.isValidTimestamp(newTimestamp, 300)) {
                    throw new RuntimeException("请求已过期，请重新操作");
                }
                
                log.debug("密码解密成功，用户: {}", user.getUsername());
            } catch (NumberFormatException e) {
                throw new RuntimeException("密码格式错误", e);
            } catch (Exception e) {
                log.error("密码解密失败: {}", e.getMessage());
                throw new RuntimeException("密码解密失败，请刷新页面重试", e);
            }
        }
        
        // 验证原密码
        if (!passwordEncoder.matches(actualOldPassword, user.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }
        
        // 检查新密码是否与原密码相同
        if (passwordEncoder.matches(actualNewPassword, user.getPassword())) {
            throw new RuntimeException("新密码不能与原密码相同");
        }
        
        // 更新密码
        String encodedNewPassword = passwordEncoder.encode(actualNewPassword);
        userService.updatePassword(user.getId(), encodedNewPassword);
        
        log.info("用户 {} 修改密码成功", user.getUsername());
    }
}

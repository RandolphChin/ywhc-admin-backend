package com.ywhc.admin.modules.auth.service.impl;

import com.ywhc.admin.common.result.ResultCode;
import com.ywhc.admin.framework.security.service.SecurityUser;
import com.ywhc.admin.modules.auth.dto.LoginDTO;
import com.ywhc.admin.modules.auth.service.AuthService;
import com.ywhc.admin.modules.auth.vo.LoginVO;
import com.ywhc.admin.modules.auth.vo.UserInfoVO;
import com.ywhc.admin.modules.system.user.entity.SysUser;
import com.ywhc.admin.modules.system.user.service.UserService;
import com.ywhc.admin.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

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

    @Override
    public LoginVO login(LoginDTO loginDTO, String clientIp) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        SysUser user = securityUser.getUser();

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException(ResultCode.USER_DISABLED.getMessage());
        }

        // 生成Token
        String accessToken = jwtUtils.generateToken(user.getUsername(), user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername(), user.getId());

        // 更新用户登录信息
        userService.updateLoginInfo(user.getId(), clientIp);

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(86400L); // 24小时

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser securityUser) {
            log.info("用户 {} 登出成功", securityUser.getUsername());
        }
        
        // 清除安全上下文
        SecurityContextHolder.clearContext();
        
        // TODO: 将Token加入黑名单（如果使用Redis）
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
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

        // TODO: 获取用户菜单树

        return userInfoVO;
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        try {
            // 验证刷新Token
            if (!jwtUtils.isValidToken(refreshToken)) {
                throw new RuntimeException(ResultCode.REFRESH_TOKEN_INVALID.getMessage());
            }

            // 从Token中获取用户信息
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            Long userId = jwtUtils.getUserIdFromToken(refreshToken);

            // 生成新的访问Token
            String newAccessToken = jwtUtils.generateToken(username, userId);
            String newRefreshToken = jwtUtils.generateRefreshToken(username, userId);

            // 构建响应
            LoginVO loginVO = new LoginVO();
            loginVO.setAccessToken(newAccessToken);
            loginVO.setRefreshToken(newRefreshToken);
            loginVO.setExpiresIn(86400L);

            return loginVO;
        } catch (Exception e) {
            log.error("Token刷新失败: {}", e.getMessage());
            throw new RuntimeException(ResultCode.REFRESH_TOKEN_INVALID.getMessage());
        }
    }
}

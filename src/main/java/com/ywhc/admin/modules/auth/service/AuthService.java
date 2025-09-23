package com.ywhc.admin.modules.auth.service;

import com.ywhc.admin.modules.auth.dto.ChangePasswordDTO;
import com.ywhc.admin.modules.auth.dto.LoginDTO;
import com.ywhc.admin.modules.auth.vo.LoginVO;
import com.ywhc.admin.modules.auth.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
public interface AuthService {
    /**
     * 用户登录
     */
    LoginVO login(
        LoginDTO loginDTO,
        String clientIp,
        HttpServletRequest request
    );

    /**
     * 用户登出
     */
    void logout(HttpServletRequest request);

    /**
     * 获取当前用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 刷新Token
     */
    LoginVO refreshToken(String refreshToken);

    /**
     * 修改密码
     */
    void changePassword(ChangePasswordDTO changePasswordDTO);
}

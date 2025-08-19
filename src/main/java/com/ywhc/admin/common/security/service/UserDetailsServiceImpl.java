package com.ywhc.admin.common.security.service;

import com.ywhc.admin.modules.system.user.entity.SysUser;
import com.ywhc.admin.modules.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户详情服务实现类
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 获取用户权限
        Set<String> permissions = userService.getUserPermissions(user.getId());
        Collection<GrantedAuthority> authorities = new HashSet<>();
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        return new SecurityUser(user, authorities);
    }
}

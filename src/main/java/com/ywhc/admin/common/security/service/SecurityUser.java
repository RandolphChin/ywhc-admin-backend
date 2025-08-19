package com.ywhc.admin.common.security.service;

import com.ywhc.admin.modules.system.user.entity.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 安全用户详情
 *
 * @author YWHC Team
 * @since 2024-01-01
 */
@Data
public class SecurityUser implements UserDetails {

    private final SysUser user;
    private final Collection<? extends GrantedAuthority> authorities;

    public SecurityUser(SysUser user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 获取用户昵称
     */
    public String getNickname() {
        return user.getNickname();
    }

    /**
     * 获取用户邮箱
     */
    public String getEmail() {
        return user.getEmail();
    }
}

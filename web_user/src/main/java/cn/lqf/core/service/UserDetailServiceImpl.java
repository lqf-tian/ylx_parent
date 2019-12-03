package cn.lqf.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lqf
 * @Date: 2019/12/2 13:59
 * 自定义认证类  在此之前负责用户密码和校验工作  先在cas和SpringSecurity集成，
 * 集成以后，用户名和密码交给css管理
 */
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        //向集合权限中添加范文权限
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",authorityList);
    }
}

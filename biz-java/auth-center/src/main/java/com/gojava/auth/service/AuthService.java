package com.gojava.auth.service;

import com.gojava.auth.common.BizException;
import com.gojava.auth.dto.LoginReq;
import com.gojava.auth.dto.LoginResp;
import com.gojava.auth.entity.User;
import com.gojava.auth.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResp login(LoginReq req) {
        User u = userMapper.findByUsername(req.getUsername());
        if (u == null) {
            // 统一提示，避免枚举用户名
            throw new BizException(1001, "用户名或密码错误");
        }
        if (u.getStatus() != null && u.getStatus() != 1) {
            throw new BizException(1003, "账号已禁用");
        }
        // bcrypt 恒定时间比对；错误直接抛
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BizException(1001, "用户名或密码错误");
        }
        String access = jwtService.issueAccess(u);
        String refresh = jwtService.issueRefresh(u);
        return new LoginResp(access, refresh, jwtService.getAccessExpire());
    }
}

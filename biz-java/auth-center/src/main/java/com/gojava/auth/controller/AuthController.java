package com.gojava.auth.controller;

import com.gojava.auth.common.Result;
import com.gojava.auth.dto.LoginReq;
import com.gojava.auth.dto.LoginResp;
import com.gojava.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

/**
 * 网关会把 /api/auth/** 剥成 /** 转发到本服务，因此这里直接映射 /login 等根路径。
 */
@RestController
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public Result<LoginResp> login(@RequestBody LoginReq req) {
        return Result.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public Result<?> refresh(@RequestBody String refreshToken) {
        // 骨架：生产应校验 Redis 中 refresh 是否存在；这里直接返回占位
        return Result.ok("refresh endpoint placeholder, see docs/03-auth-design.md");
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        // 骨架：把 jti 写 Redis 黑名单
        return Result.ok("logged out");
    }
}

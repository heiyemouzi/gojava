package com.gojava.tenant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gojava.tenant.entity.Tenant;
import com.gojava.tenant.entity.User;
import com.gojava.tenant.mapper.TenantMapper;
import com.gojava.tenant.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public TenantService(TenantMapper tenantMapper, UserMapper userMapper,
                         PasswordEncoder passwordEncoder) {
        this.tenantMapper = tenantMapper;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Tenant> tenants() {
        return tenantMapper.selectList(new LambdaQueryWrapper<Tenant>().orderByAsc(Tenant::getId));
    }

    public void createTenant(String tenantId, String name, String pkg) {
        Tenant t = new Tenant();
        t.setTenantId(tenantId);
        t.setName(name);
        t.setPackageName(pkg);
        tenantMapper.insert(t);
    }

    public List<User> users(String tenantId) {
        return userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getTenantId, tenantId)
                .orderByAsc(User::getId));
    }

    public void createUser(String username, String rawPassword, String tenantId,
                           String realName, String roles) {
        if (rawPassword == null || rawPassword.length() < 6 || rawPassword.length() > 64) {
            throw new IllegalArgumentException("密码长度需 6~64");
        }
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setTenantId(tenantId);
        u.setRealName(realName);
        u.setRoles(roles == null ? "user" : roles);
        u.setStatus(1);
        userMapper.insert(u);
    }

    public void changeRoles(Long userId, String roles) {
        userMapper.update(null,
                new LambdaUpdateWrapper<User>().eq(User::getId, userId).set(User::getRoles, roles));
    }

    public void toggleStatus(Long userId, int status) {
        userMapper.update(null,
                new LambdaUpdateWrapper<User>().eq(User::getId, userId).set(User::getStatus, status));
    }
}

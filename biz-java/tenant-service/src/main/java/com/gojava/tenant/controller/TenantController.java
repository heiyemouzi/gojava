package com.gojava.tenant.controller;

import com.gojava.tenant.entity.Tenant;
import com.gojava.tenant.entity.User;
import com.gojava.tenant.service.TenantService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TenantController {

    private final TenantService service;
    public TenantController(TenantService service) { this.service = service; }

    /** 当前登录人信息（网关透传身份头） */
    @GetMapping("/users/me")
    public Map<String, Object> me(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestHeader(value = "X-Roles", required = false) String roles) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("userId", userId);
        d.put("tenantId", tenantId);
        d.put("username", username);
        d.put("roles", roles);
        return ok(d);
    }

    /** 租户列表（管理员） */
    @GetMapping("/tenant/list")
    public Map<String, Object> tenantList() {
        return ok(service.tenants());
    }

    /** 新建租户 */
    @PostMapping("/tenant/create")
    public Map<String, Object> createTenant(@RequestBody Map<String, String> body) {
        service.createTenant(body.get("tenantId"), body.get("name"), body.getOrDefault("package", "free"));
        return ok(null);
    }

    /** 某租户下的用户列表 */
    @GetMapping("/user/list")
    public Map<String, Object> userList(@RequestParam(required = false) String tenantId,
                                        @RequestHeader(value = "X-Tenant-Id", required = false) String headerTenant) {
        String tid = tenantId != null ? tenantId : headerTenant;
        List<User> list = service.users(tid);
        return ok(list);
    }

    /** 新建用户（管理员设初始密码） */
    @PostMapping("/user/create")
    public Map<String, Object> createUser(@RequestBody Map<String, String> body) {
        try {
            service.createUser(
                    body.get("username"),
                    body.get("password"),
                    body.getOrDefault("tenantId", "default"),
                    body.get("realName"),
                    body.get("roles"));
            return ok(null);
        } catch (IllegalArgumentException e) {
            return err(4001, e.getMessage());
        }
    }

    /** 改角色 */
    @PostMapping("/user/{id}/roles")
    public Map<String, Object> changeRoles(@PathVariable Long id, @RequestBody Map<String, String> body) {
        service.changeRoles(id, body.get("roles"));
        return ok(null);
    }

    /** 启用/禁用用户 */
    @PostMapping("/user/{id}/status")
    public Map<String, Object> toggleStatus(@PathVariable Long id, @RequestParam int status) {
        service.toggleStatus(id, status);
        return ok(null);
    }

    private static Map<String, Object> ok(Object d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", 0); m.put("msg", "ok"); m.put("data", d);
        return m;
    }
    private static Map<String, Object> err(int c, String m) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("code", c); r.put("msg", m);
        return r;
    }
}

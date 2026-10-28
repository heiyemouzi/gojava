package com.gojava.tenant.entity;

public class UserVO {
    private Long id;
    private String username;
    private String tenantId;
    private String realName;
    private String roles;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

package com.gojava.auth.dto;

public class LoginResp {
    private String accessToken;
    private String refreshToken;
    private long accessExpireIn;

    public LoginResp(String a, String r, long exp) {
        this.accessToken = a; this.refreshToken = r; this.accessExpireIn = exp;
    }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long getAccessExpireIn() { return accessExpireIn; }
}

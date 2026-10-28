package com.gojava.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.gojava.auth.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${gojava.jwt.secret}")
    private String secret;
    @Value("${gojava.jwt.issuer}")
    private String issuer;
    @Value("${gojava.jwt.access-expire-seconds}")
    private long accessExpire;
    @Value("${gojava.jwt.refresh-expire-seconds}")
    private long refreshExpire;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 签 access token；claim 字段与 gateway/pkg/jwt 保持一致 */
    public String issueAccess(User u) {
        Date now = new Date();
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(u.getId()))
                .id(UUID.randomUUID().toString())
                .claim("uid", String.valueOf(u.getId()))
                .claim("tid", u.getTenantId())
                .claim("usr", u.getUsername())
                .claim("roles", u.getRoles() == null ? "" : u.getRoles())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpire * 1000))
                .signWith(key())
                .compact();
    }

    public String issueRefresh(User u) {
        Date now = new Date();
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(u.getId()))
                .claim("uid", String.valueOf(u.getId()))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpire * 1000))
                .signWith(key())
                .compact();
    }

    public long getAccessExpire() { return accessExpire; }
}

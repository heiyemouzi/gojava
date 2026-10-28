package com.gojava.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gojava.auth.mapper")
public class AuthCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthCenterApplication.class, args);
    }
}

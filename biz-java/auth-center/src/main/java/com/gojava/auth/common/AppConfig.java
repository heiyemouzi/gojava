package com.gojava.auth.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Value("${gojava.password.bcrypt-strength:12}")
    private int strength;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // bcrypt cost = strength；禁止 MD5/SHA
        return new BCryptPasswordEncoder(strength);
    }
}

package io.security.basicsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 인증 처리
        http
                .authorizeRequests()
                .anyRequest().authenticated();
        // 인가 처리
        http
                .formLogin();

        http
                .sessionManagement() // 세션 관리자 설정
                .maximumSessions(1) // 최대 세션 개수설정
                .maxSessionsPreventsLogin(true); // 중복 로그인 방지
    }
}

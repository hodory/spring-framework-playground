package io.security.basicsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
                .sessionFixation()
                .changeSessionId();
    }
}

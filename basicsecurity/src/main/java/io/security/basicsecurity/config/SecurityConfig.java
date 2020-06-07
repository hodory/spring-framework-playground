package io.security.basicsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

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
                .logout()
                .logoutUrl("/logout") // Logout Url 설정
                .logoutSuccessUrl("/login") // logout 성공시 리다이렉트
                .addLogoutHandler(new LogoutHandler() {
                    @Override
                    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                        HttpSession session = request.getSession();
                        session.invalidate();
                    }
                }) // 로그아웃 요청시 핸들러
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect("/login");
                    }
                }) // 로그아웃 성공시 핸들러
                .deleteCookies("remember-me") // 로그아웃시 삭제 할 쿠키
                .and()
                .rememberMe()
                .rememberMeParameter("remeber") // 파라미터명 default "remember-me"
                .rememberMeCookieName("remember") // 쿠키명
                .tokenValiditySeconds(3600) // 만료 시간 설정 (default 14 days)
                .alwaysRemember(true)
                .userDetailsService(userDetailsService);
    }
}

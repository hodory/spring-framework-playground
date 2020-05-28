package io.security.basicsecurity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
                .formLogin()
//                .loginPage("/loginPage") // 로그인 페이지 설정
                .defaultSuccessUrl("/") //  로그인 성공시 이동 할 페이지
                .failureUrl("/login") // 로그인 실패시 이동 할 페이지
                .usernameParameter("userId") // 아이디를 입력 받을 파라미터명
                .passwordParameter("passwd") // 비밀번호를 입력 받을 파라미터명
                .loginProcessingUrl("/login_proc")//form tag의 action URL
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        System.out.println("authentication : '" + authentication.getName() + "'");
                        response.sendRedirect("/");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        System.out.println("execption : '" + exception.getMessage() + "'");
                        response.sendRedirect("/login");
                    }
                })
                .permitAll()
        ;
    }
}

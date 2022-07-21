package com.example.socialCrawler.config;

import com.example.socialCrawler.security.RestAuthenticationEntryPoint;
import com.example.socialCrawler.security.jwt.TokenAuthenticationFilter;
import com.example.socialCrawler.security.oauth2.CustomOauth2UserService;
import com.example.socialCrawler.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.socialCrawler.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.example.socialCrawler.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.example.socialCrawler.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;

    private final CustomOauth2UserService customOauth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    // TODO: OAuth2

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        // JWT Token 필터 등록
        return new TokenAuthenticationFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 패스워드 hash 인코더
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        // OAuth2 인증을 위한 Request/Response 속 Cookie 세팅
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Spring Security
        // 유저 인증을 위해 userDetailsService 와
        // 패스워드 검증을 위한 passwordEncoder 등록
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .csrf()
                    .disable()  // CORS, Session 인증, CSRF 미사용
                .formLogin()
                    .disable()  // Form 로그인 미사용
                .httpBasic()
                    .disable()  // HTTP basic auth(브라우저기반) 미사용
                .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())   // 인증, 인가 실패시 처리
                    .and()
                .authorizeRequests()
                    .antMatchers("/",
                            "/error",
                            "/favicon.ico",
                            "/**/*.png",
                            "/**/*.gif",
                            "/**/*.svg",
                            "/**/*.jpg",
                            "/**/*.html",
                            "/**/*.css",
                            "/**/*.js")
                .permitAll()
                    .antMatchers("/auth/**", "/oauth2/**")
                .permitAll()    // auth, Oauth, 기타 asset 은 인증없이 접근허용
                    .anyRequest()
                .authenticated()    // 그 외 요청은 인증필요
                    .and()
                .oauth2Login()  // OAuth2 설정
                    .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")   // OAuth2 로그인 URL
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository()) // OAuth2 인증 요청 쿠기처리 repository
                        .and()
                    .redirectionEndpoint()
                        .baseUri("/oauth2/callback/*")  // OAuth2 로그인 redirect URL
                        .and()
                    .userInfoEndpoint()
                        .userService(customOauth2UserService)   // OAuth2 유저 정보 처리 서비스
                        .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler) // OAuth2 인증 성공/실패 handler
                    .failureHandler(oAuth2AuthenticationFailureHandler);

        // Username/Password 이전 JWT 인증방식 filter 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}

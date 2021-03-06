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
        // JWT Token ?????? ??????
        return new TokenAuthenticationFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        // ???????????? hash ?????????
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        // OAuth2 ????????? ?????? Request/Response ??? Cookie ??????
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Spring Security
        // ?????? ????????? ?????? userDetailsService ???
        // ???????????? ????????? ?????? passwordEncoder ??????
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
                    .disable()  // CORS, Session ??????, CSRF ?????????
                .formLogin()
                    .disable()  // Form ????????? ?????????
                .httpBasic()
                    .disable()  // HTTP basic auth(??????????????????) ?????????
                .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())   // ??????, ?????? ????????? ??????
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
                .permitAll()    // auth, Oauth, ?????? asset ??? ???????????? ????????????
                    .anyRequest()
                .authenticated()    // ??? ??? ????????? ????????????
                    .and()
                .oauth2Login()  // OAuth2 ??????
                    .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")   // OAuth2 ????????? URL
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository()) // OAuth2 ?????? ?????? ???????????? repository
                        .and()
                    .redirectionEndpoint()
                        .baseUri("/oauth2/callback/*")  // OAuth2 ????????? redirect URL
                        .and()
                    .userInfoEndpoint()
                        .userService(customOauth2UserService)   // OAuth2 ?????? ?????? ?????? ?????????
                        .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler) // OAuth2 ?????? ??????/?????? handler
                    .failureHandler(oAuth2AuthenticationFailureHandler);

        // Username/Password ?????? JWT ???????????? filter ??????
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}

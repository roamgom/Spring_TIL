package com.example.socialCrawler.security.oauth2;

import com.example.socialCrawler.exception.UserAlreadyExistsException;
import com.example.socialCrawler.utils.CookieUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.socialCrawler.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    // TODO: SimpleUrlAuthenticationFailureHandler 에서 AuthenticationFailureHandler 로 변경

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private final MappingJackson2HttpMessageConverter httpMessageConverter;

    private static final MediaType CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON;

    @Autowired
    public OAuth2AuthenticationFailureHandler(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository, MappingJackson2HttpMessageConverter httpMessageConverter) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.httpMessageConverter = httpMessageConverter;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        // TODO: Exception 종류에 따른 HTTP status code 와 에러 메시지 분류
        Map<String, Object> data = new HashMap<>();
        data.put(
                "errorMessage",
                exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        HttpOutputMessage message = new ServletServerHttpResponse(response);
        httpMessageConverter.write(data, CONTENT_TYPE_JSON, message);
    }

    @Deprecated
    public void redirectToFailureUrl(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // SimpleUrlAuthenticationFailureHandler 상속 기준에서 redirect 처리 시
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

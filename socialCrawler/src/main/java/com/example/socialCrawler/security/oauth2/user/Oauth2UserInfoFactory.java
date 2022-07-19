package com.example.socialCrawler.security.oauth2.user;

import com.example.socialCrawler.domain.entity.AuthProvider;
import com.example.socialCrawler.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class Oauth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {

        if (registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())) {
            return new FacebookOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException(registrationId + " OAuth Login is not supported.");
        }
    }
}

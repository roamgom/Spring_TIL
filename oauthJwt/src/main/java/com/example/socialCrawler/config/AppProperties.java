package com.example.socialCrawler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    // JWT auth 와 OAuth2 설정을 위한 Properties
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();

    public static class Auth {
        // JWT Auth
        // secretToken: JWT 토큰 발급을 위한 secretKey
        // tokenExpirationMilliSec: JWT 만기 시간(ms)
        private String secretToken;
        private long tokenExpirationMilliSec;

        public String getSecretToken() {
            return secretToken;
        }

        public void setSecretToken(String secretToken) {
            this.secretToken = secretToken;
        }

        public long getTokenExpirationMilliSec() {
            return tokenExpirationMilliSec;
        }

        public void setTokenExpirationMilliSec(long tokenExpirationMilliSec) {
            this.tokenExpirationMilliSec = tokenExpirationMilliSec;
        }
    }

    public static final class OAuth2 {
        // authorizedRedirectUris: OAuth2 인증시 허용된 redirect URI 목록 (application.yml)
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }

    public Auth getAuth() {
        return auth;
    }

    public OAuth2 getOauth2() {
        return oauth2;
    }

}

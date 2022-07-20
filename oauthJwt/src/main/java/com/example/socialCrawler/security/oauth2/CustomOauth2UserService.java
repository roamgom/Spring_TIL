package com.example.socialCrawler.security.oauth2;

import com.example.socialCrawler.domain.entity.AuthProvider;
import com.example.socialCrawler.domain.entity.OAuthInfo;
import com.example.socialCrawler.domain.entity.User;
import com.example.socialCrawler.domain.repository.JpaOAuthInfoRepository;
import com.example.socialCrawler.domain.repository.UserRepository;
import com.example.socialCrawler.exception.OAuth2AuthenticationProcessingException;
import com.example.socialCrawler.security.UserPrincipal;
import com.example.socialCrawler.security.oauth2.user.OAuth2UserInfo;
import com.example.socialCrawler.security.oauth2.user.Oauth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JpaOAuthInfoRepository oAuthInfoRepository;

    public CustomOauth2UserService(UserRepository userRepository, JpaOAuthInfoRepository oAuthInfoRepository) {
        this.userRepository = userRepository;
        this.oAuthInfoRepository = oAuthInfoRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationProcessingException ex) {
            // TODO: 왜 AuthenticationException으로는 catch가 안되는것일까
            throw ex;
        } catch (Exception ex) {
            // AuthenticationException 인스턴스가 OAuth2AuthenticationFailureHandler 를 작동시킨다
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest request, OAuth2User oAuth2User) {
        AuthProvider authProvider = AuthProvider.valueOf(request.getClientRegistration().getRegistrationId());
        OAuth2UserInfo oAuth2UserInfo = Oauth2UserInfoFactory.getOAuth2UserInfo(request.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        // OAuthInfo에서 조회하여 비교
        Optional<OAuthInfo> oAuthInfoOptional = oAuthInfoRepository.findByEmailAndAuthProvider(oAuth2UserInfo.getEmail(), authProvider);
        OAuthInfo oAuthInfo;
        User user;

        if (oAuthInfoOptional.isPresent()) {
            oAuthInfo = oAuthInfoOptional.get();

            if (!oAuthInfo.getAuthProvider().equals(AuthProvider.valueOf(request.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException(authProvider + " OAuth Login is not supported.");
            }
            user = oAuthInfo.getUser();
        } else {
            user = registerNewUser(request, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest request, OAuth2UserInfo oAuth2UserInfo) {
        // 새로운 OAuth2 유저 등록
        // 유저 등록 & OAuth 인증정보 저장
        User user = User.builder()
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .build();
        userRepository.save(user);

        OAuthInfo oAuthInfo = OAuthInfo.builder()
                .authProvider(AuthProvider.valueOf(request.getClientRegistration().getRegistrationId()))
                .oauthId(oAuth2UserInfo.getId())
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .user(user)
                .build();

        oAuthInfoRepository.save(oAuthInfo);
        return user;
    }
}

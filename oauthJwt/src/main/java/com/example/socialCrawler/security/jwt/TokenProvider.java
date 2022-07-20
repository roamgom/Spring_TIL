package com.example.socialCrawler.security.jwt;

import com.example.socialCrawler.config.AppProperties;
import com.example.socialCrawler.security.UserPrincipal;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMilliSec());

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getSecretToken())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getSecretToken())
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getSecretToken())
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException exception) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT Token");
        } catch (ExpiredJwtException exception) {
            log.error("Expired JWT Token");
        } catch (UnsupportedJwtException exception) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException exception) {
            log.error("JWT claims is empty");
        }
        return false;
    }
}

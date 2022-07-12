package com.example.socialCrawler.controller;

import java.security.Principal;
import java.util.Map;
import org.springframework.security.oauth2.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OauthDemoController {

    @GetMapping
    public String getUser(final Principal user) {
        Map map = (Map) ((OAuth2Authentication))
    }
}

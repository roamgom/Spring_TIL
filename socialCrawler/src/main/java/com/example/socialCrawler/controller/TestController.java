package com.example.socialCrawler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {
    @GetMapping("/welcome")
    public String welcome() {
        return "WELCOME!";
    }
}

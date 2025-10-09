package com.i2.kproject_2025_2.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 최소 예제: 한 개의 GET 엔드포인트
@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public String hello() {
        return "hello";
    }
}

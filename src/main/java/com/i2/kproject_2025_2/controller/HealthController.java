package com.i2.kproject_2025_2.controller;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

// ✅ 단순 헬스체크 컨트롤러
@RestController
@RequestMapping("/api")
public class HealthController {

    // ⚙️ DB 연결 확인용 (나중에 활성화할 코드)
    // @Autowired
    // private JdbcTemplate jdbc;

    @GetMapping("/health")
    public Map<String, Object> health() {

        // 기본 응답
        String status = "ok";
        String dbStatus = "disabled (local mode)";

        /*
        // 📦 나중에 DB 연결되면 이 부분만 주석 해제
        try {
            jdbc.queryForObject("SELECT 1", Integer.class);
            dbStatus = "connected";
        } catch (Exception e) {
            dbStatus = "error: " + e.getMessage();
            status = "warning";
        }
        */

        return Map.of(
                "status", status,
                "time", LocalDateTime.now().toString(),
                "db", dbStatus
        );
    }
}

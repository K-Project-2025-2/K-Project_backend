plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.I2"
version = "0.0.1-SNAPSHOT"
description = "K-Project_2025_2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}


//의존성...
dependencies {
    // ── Spring 기본 웹/운영 ─────────────────────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-actuator")       // 애플리케이션 헬스체크/메트릭/엔드포인트
    implementation("org.springframework.boot:spring-boot-starter-web")            // Spring MVC + 내장 톰캣(REST API)
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.3")
    runtimeOnly("com.h2database:h2")
    // ── 데이터 접근 ─────────────────────────────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")       // JPA/Hibernate 기반 ORM
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")                            // MariaDB JDBC 드라이버
    implementation("org.springframework.boot:spring-boot-starter-data-redis")     // Redis 연결(캐시/세션/레이트리밋 등)

    // ── 보안/인증 ───────────────────────────────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-security")       // 인증/인가 기본 프레임워크(BCrypt 포함)
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")                              // JWT 생성/파싱 API
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")                                   // JWT 구현체
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")                             // JWT에서 Jackson(Claims 직렬화/역직렬화)

    // ── 입력 검증/메일 ──────────────────────────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-validation")     // Bean Validation(@Valid, @Email 등)
    implementation("org.springframework.boot:spring-boot-starter-mail")           // JavaMailSender(이메일 인증/알림)


    // ── 문서화/개발 편의 ────────────────────────────────────────────────────
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")     // Swagger UI(OpenAPI 3 스펙 자동 문서화)
    developmentOnly("org.springframework.boot:spring-boot-devtools")               // 핫스왑/자동 재시작(개발 환경 전용)

    // ── 보일러플레이트 감소 ────────────────────────────────────────────────
    compileOnly("org.projectlombok:lombok")                                        // Lombok 애너테이션(컴파일 시에만 필요)
    annotationProcessor("org.projectlombok:lombok")                                // Lombok 애너테이션 프로세서

    // ── 테스트 ─────────────────────────────────────────────────────────────
    testImplementation("org.springframework.boot:spring-boot-starter-test")        // JUnit/Jupiter, AssertJ, MockMvc 등
    testImplementation("org.springframework.security:spring-security-test")        // Security 테스트 유틸(Mock 인증 등)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")                  // JUnit 플랫폼 런처(IDE/빌드 연동)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

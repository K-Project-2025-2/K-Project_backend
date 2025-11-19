package com.i2.kproject_2025_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
@EnableJpaRepositories(basePackages = "com.i2.kproject_2025_2")
public class KProject20252Application {

    public static void main(String[] args) {
        SpringApplication.run(KProject20252Application.class, args);
    }

}

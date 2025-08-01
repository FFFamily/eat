package com.tutu.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.tutu"})
@MapperScan("com.tutu.*.mapper")
@EnableCaching
@EnableConfigurationProperties
public class EatApplication {
    public static void main(String[] args) {
        SpringApplication.run(EatApplication.class, args);
    }
}

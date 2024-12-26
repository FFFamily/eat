package com.tutu.eat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class EatApplication {
    public static void main(String[] args) {
        SpringApplication.run(EatApplication.class, args);
    }
}

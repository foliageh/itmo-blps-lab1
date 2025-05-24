package com.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CourierNodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourierNodeApplication.class, "--spring.profiles.active=courier");
    }
}

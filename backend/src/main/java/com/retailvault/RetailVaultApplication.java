package com.retailvault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RetailVaultApplication {
    public static void main(String[] args) {
        SpringApplication.run(RetailVaultApplication.class, args);
    }
}

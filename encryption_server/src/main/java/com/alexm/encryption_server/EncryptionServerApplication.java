package com.alexm.encryption_server;

import com.alexm.encryption_server.security.EncryptionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EncryptionServerApplication {
    @Bean
    EncryptionManager encryptionManager() {
        return new EncryptionManager();
    }

    public static void main(String[] args) {
        SpringApplication.run(EncryptionServerApplication.class, args);
    }

}

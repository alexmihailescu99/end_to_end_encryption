package com.alexm.client;

import com.squareup.okhttp.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {
    @Bean
    RSA rsa() {
        return new RSA();
    }

    @Bean
    OkHttpClient getOkHttpClient() {
        return new OkHttpClient();
    }

    public static void main(String[] args) { SpringApplication.run(ClientApplication.class, args); }

}

package com.example.ewalletapplication.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UserServiceConfiguration {

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}

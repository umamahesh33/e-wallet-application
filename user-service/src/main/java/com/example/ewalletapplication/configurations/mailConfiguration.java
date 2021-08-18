package com.example.ewalletapplication.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class mailConfiguration {

    @Bean
    JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("Ewallet.noreply@gmail.com");
        javaMailSender.setPassword("Umamahesh@33");
        Properties properties=javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.starttls.enable", true);
        return javaMailSender;
    }

    @Bean
    SimpleMailMessage simpleMailMessage(){
        return new SimpleMailMessage();
    }
}

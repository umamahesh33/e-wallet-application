package com.example.ewalletapplication.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Properties;

@Configuration
public class kafkaConfiguration {

    @Bean
    Properties setKafkaProps(){
        Properties properties=new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);

        return properties;
    }

    @Bean
    ProducerFactory<String,String> getKafkaProducer(){
        return new DefaultKafkaProducerFactory(setKafkaProps());
    }

    @Bean
    KafkaTemplate<String,String> kafkaTemplate(){
        return new KafkaTemplate<>(getKafkaProducer());
    }

    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}

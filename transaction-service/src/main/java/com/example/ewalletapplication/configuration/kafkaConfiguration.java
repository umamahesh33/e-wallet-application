package com.example.ewalletapplication.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.Properties;

@Configuration
public class kafkaConfiguration {

    @Bean
    Properties setProperties(){
        Properties properties=new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringSerializer.class);

        return properties;
    }

    @Bean
    ProducerFactory<String,String> producerFactory(){
        return new DefaultKafkaProducerFactory(setProperties());
    }

    @Bean
    KafkaTemplate<String,String> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    ConsumerFactory<String ,String > consumerFactory(){
        return new DefaultKafkaConsumerFactory(setProperties());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String,String> kafkaListener=new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListener.setConsumerFactory(consumerFactory());
        return kafkaListener;
    }

    @Bean
    ObjectMapper objectMapper(){return new ObjectMapper();}
}

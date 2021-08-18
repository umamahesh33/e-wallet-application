package com.example.ewalletapplication;

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
public class KafkaConfiguration {

    @Bean
    Properties kafkaProperties(){
        Properties properties=new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringSerializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringSerializer.class);

        return properties;
    }

    ConsumerFactory<String,String> consumerFactory(){
        return new DefaultKafkaConsumerFactory(kafkaProperties());
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String,String> getKafkaListner(){
        ConcurrentKafkaListenerContainerFactory kafkaListner=new ConcurrentKafkaListenerContainerFactory();
        kafkaListner.setConsumerFactory(consumerFactory());
        return kafkaListner;
    }

    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}

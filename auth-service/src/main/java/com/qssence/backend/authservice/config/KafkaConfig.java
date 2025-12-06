package com.qssence.backend.authservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.producer.auth-topic}")
    private String authTopic;

    @Value("${kafka.producer.group-topic}")
    private String groupTopic;

    @Value("${kafka.producer.role-topic}")
    private String roleTopic;

    @Value("${kafka.producer.user-topic}")
    private String userTopic;

    @Bean
    public NewTopic authEventsTopic() {
        return new NewTopic(authTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic groupEventsTopic() {
        return new NewTopic(groupTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic roleEventsTopic() {
        return new NewTopic(roleTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic(userTopic, 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

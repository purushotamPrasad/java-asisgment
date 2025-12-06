package com.qssence.backend.authservice.kafka.producer;

import com.qssence.backend.authservice.kafka.event.LoginRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class AuthProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthProducer.class);

    @Value("${kafka.producer.auth-topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(LoginRequestEvent loginEvent) {
        LOGGER.info(String.format("Sending auth event: %s", loginEvent.toString()));
        try {
            Message<LoginRequestEvent> message = MessageBuilder
                    .withPayload(loginEvent)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .build();
            kafkaTemplate.send(message);
            LOGGER.info("Auth event sent successfully.");
        } catch (Exception e) {
            LOGGER.error("Error sending auth event: {}", e.getMessage());
        }
    }
}



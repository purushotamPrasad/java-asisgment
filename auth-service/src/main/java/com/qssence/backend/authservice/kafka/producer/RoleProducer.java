package com.qssence.backend.authservice.kafka.producer;

import com.qssence.backend.authservice.kafka.event.LoginRequestEvent;
import com.qssence.backend.authservice.kafka.event.RoleRequestEvent;
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
public class RoleProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleProducer.class);

    @Value("${kafka.producer.role-topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendRoleEvent(RoleRequestEvent roleEvent) {
        LOGGER.info(String.format("Sending role event: %s", roleEvent.toString()));
        try {
            Message<RoleRequestEvent> message = MessageBuilder
                    .withPayload(roleEvent)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .build();
            kafkaTemplate.send(message);
            LOGGER.info("Role event sent successfully.");
        } catch (RuntimeException e) {
            LOGGER.error("Error sending role event: {}", e.getMessage());
        }
    }
}

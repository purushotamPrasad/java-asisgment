package com.qssence.backend.authservice.kafka.producer;

import com.qssence.backend.authservice.kafka.event.UserRequestEvent;
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
public class UserProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProducer.class);

    @Value("${kafka.producer.user-topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

   public void sendMessage(UserRequestEvent userEvent) {
        LOGGER.info(String.format("Sending user event: %s", userEvent.toString()));
        try {
            Message<UserRequestEvent> message = MessageBuilder
                    .withPayload(userEvent)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .build();
            kafkaTemplate.send(message);
            LOGGER.info("User event sent successfully.");
        } catch (Exception e) {
            LOGGER.error("Error sending user event: {}", e.getMessage());
        }
    }
}

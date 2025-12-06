package com.qssence.backend.authservice.kafka.producer;

import com.qssence.backend.authservice.kafka.event.GroupRequestEvent;
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
public class GroupProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupProducer.class);

    @Value("${kafka.producer.group-topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendGroupEvent(GroupRequestEvent groupEvent) {
        LOGGER.info(String.format("Sending group event: %s", groupEvent.toString()));
        try {
            Message<GroupRequestEvent> message = MessageBuilder
                    .withPayload(groupEvent)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .build();
            kafkaTemplate.send(message);
            LOGGER.info("Group event sent successfully.");
        } catch (Exception e) {
            LOGGER.error("Error sending group event: {}", e.getMessage());
        }
    }
}

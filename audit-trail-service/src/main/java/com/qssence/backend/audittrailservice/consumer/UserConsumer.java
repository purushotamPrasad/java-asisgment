package com.qssence.backend.audittrailservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.audittrailservice.entity.UserLog;
import com.qssence.backend.audittrailservice.entity.event.UserRequestEvent;
import com.qssence.backend.audittrailservice.repository.UserLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserConsumer.class);
    private final ObjectMapper objectMapper;
    private final UserLogRepository userLogRepository;

    @Autowired
    public UserConsumer(ObjectMapper objectMapper, UserLogRepository userLogRepository) {
        this.objectMapper = objectMapper;
        this.userLogRepository = userLogRepository;
    }

    @KafkaListener(topics = "user-events", groupId = "user-consumer-group")
    public void receiveMessage(String message) {
        try {
            LOGGER.info("Received message from Kafka: {}", message);

            UserRequestEvent userEvent = objectMapper.readValue(message, UserRequestEvent.class);
            LOGGER.info("User event received in audit trail service: {}", userEvent.toString());

            UserLog userLog = new UserLog();
            userLog.setMessage(userEvent.getMessage());
            userLog.setStatus(userEvent.getStatus());
            userLog.setTimestamp(userEvent.getTimestamp());
            userLog.setIpAddress(userEvent.getIpAddress());
            userLog.setUsername(userEvent.getUsername());

            userLogRepository.save(userLog);
            LOGGER.info("User log saved to MongoDB: {}", userLog.toString());
        } catch (Exception e) {
            LOGGER.error("Error processing Kafka message: {}", message, e);
        }
    }
}

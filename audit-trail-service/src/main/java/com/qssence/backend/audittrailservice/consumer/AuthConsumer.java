package com.qssence.backend.audittrailservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.audittrailservice.entity.AuthLog;
import com.qssence.backend.audittrailservice.entity.event.LoginRequestEvent;
import com.qssence.backend.audittrailservice.repository.AuthLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuthConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthConsumer.class);
    private final ObjectMapper objectMapper;
    private final AuthLogRepository authLogRepository;

    @Autowired
    public AuthConsumer(ObjectMapper objectMapper, AuthLogRepository authLogRepository) {
        this.objectMapper = objectMapper;
        this.authLogRepository = authLogRepository;
    }

    @KafkaListener(topics = "auth-events", groupId = "auth-consumer-group")
    public void receiveMessage(String message) {
        try {
            LOGGER.info("Received message from Kafka: {}", message);

            LoginRequestEvent loginEvent = objectMapper.readValue(message, LoginRequestEvent.class);
            LOGGER.info("Auth event received in audit trail service: {}", loginEvent.toString());

            AuthLog authLog = new AuthLog();
            authLog.setMessage(loginEvent.getMessage());
            authLog.setStatus(loginEvent.getStatus());
            authLog.setTimestamp(loginEvent.getTimestamp());
            authLog.setIpAddress(loginEvent.getIpAddress());
            authLog.setUsername(loginEvent.getUsername());

            authLogRepository.save(authLog);
            LOGGER.info("Auth log saved to MongoDB: {}", authLog.toString());
        } catch (Exception e) {
            LOGGER.error("Error processing Kafka message: {}", message, e);
        }
    }
}

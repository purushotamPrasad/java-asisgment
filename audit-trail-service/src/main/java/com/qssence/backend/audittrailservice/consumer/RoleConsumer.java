package com.qssence.backend.audittrailservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.audittrailservice.entity.RoleLog;
import com.qssence.backend.audittrailservice.entity.event.RoleRequestEvent;
import com.qssence.backend.audittrailservice.repository.RoleLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RoleConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleConsumer.class);
    private final ObjectMapper objectMapper;
    private final RoleLogRepository roleLogRepository;

    @Autowired
    public RoleConsumer(ObjectMapper objectMapper, RoleLogRepository roleLogRepository) {
        this.objectMapper = objectMapper;
        this.roleLogRepository = roleLogRepository;
    }

    @KafkaListener(topics = "role-events", groupId = "role-consumer-group")
    public void receiveMessage(String message) {
        try {
            LOGGER.info("Received message from Kafka: {}", message);

            RoleRequestEvent roleEvent = objectMapper.readValue(message, RoleRequestEvent.class);
            LOGGER.info("Role event received in audit trail service: {}", roleEvent.toString());

            RoleLog roleLog = new RoleLog();
            roleLog.setMessage(roleEvent.getMessage());
            roleLog.setStatus(roleEvent.getStatus());
            roleLog.setTimestamp(roleEvent.getTimestamp());
            roleLog.setIpAddress(roleEvent.getIpAddress());
            roleLog.setRoleId(roleEvent.getRolesId());
            roleLog.setName(roleEvent.getName());

            roleLogRepository.save(roleLog);
            LOGGER.info("Role log saved to MongoDB: {}", roleLog.toString());
        } catch (Exception e) {
            LOGGER.error("Error processing Kafka message: {}", message, e);
        }
    }
}

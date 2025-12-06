package com.qssence.backend.audittrailservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qssence.backend.audittrailservice.entity.GroupLog;
import com.qssence.backend.audittrailservice.entity.event.GroupRequestEvent;
import com.qssence.backend.audittrailservice.repository.GroupLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class GroupConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupConsumer.class);
    private final ObjectMapper objectMapper;
    private final GroupLogRepository groupLogRepository;

    @Autowired
    public GroupConsumer(ObjectMapper objectMapper, GroupLogRepository groupLogRepository) {
        this.objectMapper = objectMapper;
        this.groupLogRepository = groupLogRepository;
    }

    @KafkaListener(topics = "group-events", groupId = "group-consumer-group")
    public void receiveMessage(String message) {
        try {
            LOGGER.info("Received message from Kafka: {}", message);

            GroupRequestEvent groupEvent = objectMapper.readValue(message, GroupRequestEvent.class);
            LOGGER.info("Group event received in audit trail service: {}", groupEvent.toString());

            GroupLog groupLog = new GroupLog();
            groupLog.setMessage(groupEvent.getMessage());
            groupLog.setStatus(groupEvent.getStatus());
            groupLog.setTimestamp(groupEvent.getTimestamp());
            groupLog.setIpAddress(groupEvent.getIpAddress());
            groupLog.setGroupId(groupEvent.getGroupsId());
            groupLog.setName(groupEvent.getName());

            groupLogRepository.save(groupLog);
            LOGGER.info("Group log saved to MongoDB: {}", groupLog.toString());
        } catch (Exception e) {
            LOGGER.error("Error processing Kafka message: {}", message, e);
        }
    }
}

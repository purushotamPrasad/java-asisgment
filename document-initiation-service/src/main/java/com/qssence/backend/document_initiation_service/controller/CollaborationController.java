package com.qssence.backend.document_initiation_service.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class CollaborationController {
    @MessageMapping("/doc-edit")
    @SendTo("/topic/doc-edit")
    public String broadcastEdit(String delta) {
        return delta;
    }
}

package com.example.chatApp.controller;

import com.example.chatApp.model.Message;
import com.example.chatApp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageService.saveMessage(message); // Save message using thread-safe operation
        return message; // Broadcast to "/chatroom/public"
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageService.saveMessage(message); // Save message using thread-safe operation
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        return message;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> saveMessage(@RequestBody Message message) {
        // Calls the service to save the message
        messageService.saveMessage(message);
        // Returns a 200 OK response with no content
        return ResponseEntity.ok().build();
    }

    @PutMapping("/message/{id}/edit")
    public ResponseEntity<Message> editMessage(@PathVariable Long id, @RequestBody Message updatedMessage) {
        Message message = messageService.editMessage(id, updatedMessage.getMessage());

        // Broadcast the edited message to all clients
        simpMessagingTemplate.convertAndSend("/chatroom/public", message);

        // Broadcast to the specific user if it's a private message
        if (message.getReceiverName() != null) {
            simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
        }

        return ResponseEntity.ok(message);
    }
}

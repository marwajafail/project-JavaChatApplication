package com.example.chatApp.service;

import com.example.chatApp.model.Message;
import com.example.chatApp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageService {
    // Connects to the database to handle messages
    @Autowired
    private MessageRepository messageRepository;
    //Saves a message to the database.
    // Using synchronized to ensure thread safety
    public synchronized void saveMessage(Message message) {
        messageRepository.save(message);
    }

    public Message editMessage(Long id, String newContent) { //Updates the content of a message by its ID.
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) { // Checks if the message exists
            Message message = optionalMessage.get(); // Gets the message
            message.setMessage(newContent); // Changes the message content
            return messageRepository.save(message); // // Saves the updated message
        } else {
            throw new RuntimeException("Message not found");
        }
    }
}

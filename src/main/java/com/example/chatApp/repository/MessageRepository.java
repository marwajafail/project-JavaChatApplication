package com.example.chatApp.repository;

import com.example.chatApp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverNameOrderByTimestampAsc(String receiverName);
}

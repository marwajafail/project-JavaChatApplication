package com.example.chatApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Message {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String senderName;

    @Column
    private String receiverName;

    @Column
    private String message;

    @Column
    private LocalDateTime timestamp;

    @Column
    private Status status;

    @Column
    private boolean isRead;

    @Column
    private boolean canEdit;

    @Column
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.isRead = false;
        this.canEdit = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = LocalDateTime.now();
    }
}

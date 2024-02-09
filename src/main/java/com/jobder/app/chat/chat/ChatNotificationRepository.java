package com.jobder.app.chat.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatNotificationRepository extends MongoRepository<ChatNotification, String> {
    List<ChatNotification> findByToUserIdAndDeliveredFalse(String toUserId);
}

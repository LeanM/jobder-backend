package com.jobder.app.chat.chatroom;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
    @Aggregation(pipeline = {
            "{ '$match': { 'senderId' : ?0 } }",
            "{ '$sort' : { 'lastMessageTimestamp' : -1 } }",
    })
    List<ChatRoom> findBySenderIdOrderByLastMessage(String senderId);
}

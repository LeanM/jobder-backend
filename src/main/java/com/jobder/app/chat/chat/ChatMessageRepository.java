package com.jobder.app.chat.chat;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByChatId(String chatId);

    @Query("{ 'chatId' : ?0 , 'recipientId' : ?1, 'seenByRecipient' : false }")
    List<ChatMessage> findByChatIdAndNotSeenByRecipient(String chatId, String recipientId);

    @Aggregation(pipeline = {
            "{ '$match': { 'chatId' : ?0 } }",
            "{ '$sort' : { 'timestamp' : 1 } }",
            "{ '$limit' : 1 }",
    })
    ChatMessage findByChatIdAndOldestTimestamp(String chatId);
}

package com.jobder.app.chat.chatroom;

import com.jobder.app.authentication.repositories.UserRepository;
import com.jobder.app.chat.chat.ChatMessage;
import com.jobder.app.chat.chat.ChatMessageRepository;
import com.jobder.app.chat.exceptions.ChatRoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private static final int MESSAGE_LIMIT = 30;

    public Optional<String> getChatRoomId(
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists
    ) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId,recipientId);
        Optional<String> chatIdentifier = chatRoom.map(ChatRoom::getChatId);
        return chatIdentifier
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    private String createChatId(String senderId, String recipientId) {
        //chequear que el recipient exista y sea worker
        var chatId = String.format("%s_%s", senderId, recipientId);

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .state(ChatRoomState.NEW)
                .messageQuantity(0)
                .lastMessageTimestamp(new Date())
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .state(ChatRoomState.NEW)
                .messageQuantity(0)
                .lastMessageTimestamp(new Date())
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }

    public List<ChatRoom> getUserChatRoomsOrderedByLastMessage(String userId){
        return chatRoomRepository.findBySenderIdOrderByLastMessage(userId);
    }

    public ChatRoom getUserChatRoomWithOtherUser(String userId, String otherUserId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(userId, otherUserId);
        return chatRoom.orElse(null);
    }

    public void updateChatRoomOnMessage(ChatMessage newMessage) throws ChatRoomException {
        //Busco ambos chatrooms
        ChatRoom chatRoomRecipient = chatRoomRepository.findBySenderIdAndRecipientId(newMessage.getRecipientId(), newMessage.getSenderId()).orElseThrow( () -> new ChatRoomException("No chatroom between users!"));
        ChatRoom chatRoomSender = chatRoomRepository.findBySenderIdAndRecipientId(newMessage.getSenderId(), newMessage.getRecipientId()).orElseThrow( () -> new ChatRoomException("No chatroom between users!"));

        //Seteo como no visto y actualizo la timestamp del chatroom del que recibe el mensaje
        chatRoomRecipient.setMessageQuantity(chatRoomRecipient.getMessageQuantity() + 1);
        if(!chatRoomRecipient.getState().name().equals("NEW")) {
            chatRoomRecipient.setState(ChatRoomState.UNSEEN);
            chatRoomRecipient.setLastMessageTimestamp(newMessage.getTimestamp());
            chatRoomRepository.save(chatRoomRecipient);
        } else {
            chatRoomRecipient.setLastMessageTimestamp(newMessage.getTimestamp());
            chatRoomRepository.save(chatRoomRecipient);
        }

        //Actualizo la timestamp del chatroom del que envia el mensaje
        chatRoomSender.setMessageQuantity(chatRoomSender.getMessageQuantity() + 1);
        chatRoomSender.setLastMessageTimestamp(newMessage.getTimestamp());
        //setSeenChatRoomOnOpenChat(newMessage.getSenderId(), newMessage.getRecipientId());
        chatRoomSender.setState(ChatRoomState.SEEN);
        chatRoomRepository.save(chatRoomSender);

        verifyChatMessagesLimit(chatRoomSender, chatRoomRecipient);
    }

    private void verifyChatMessagesLimit(ChatRoom chatRoomSender, ChatRoom chatRoomRecipient){
        if(chatRoomSender.getMessageQuantity() > MESSAGE_LIMIT){
            ChatMessage oldestMessage = chatMessageRepository.findByChatIdAndOldestTimestamp(chatRoomSender.getChatId());
            chatMessageRepository.delete(oldestMessage);
            chatRoomSender.setMessageQuantity(chatRoomSender.getMessageQuantity() - 1);
            chatRoomRecipient.setMessageQuantity(chatRoomRecipient.getMessageQuantity() - 1);
        }
    }

    public void setSeenChatRoomOnOpenChat(String openerId, String recipientId){
        //Marco el chatroom como visto ya que se solicitaron los mensajes
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(openerId,recipientId);
        chatRoom.ifPresent(chatRoom1 -> {chatRoom1.setState(ChatRoomState.SEEN);setSeenMessagesOnSeenChatroom(chatRoom1.getChatId(), openerId);chatRoomRepository.save(chatRoom1);});
    }

    public void setSeenMessagesOnSeenChatroom(String chatId, String openerId){
        List<ChatMessage> notSeenMessages = chatMessageRepository.findByChatIdAndNotSeenByRecipient(chatId, openerId);
        for(ChatMessage message : notSeenMessages){
            message.setSeenByRecipient(true);
            chatMessageRepository.save(message);
        }
    }

    public void deleteChatRooms(String senderId, String recipientId) throws ChatRoomException {
        Optional<ChatRoom> firstChatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId,recipientId);
        Optional<ChatRoom> secondChatRoom = chatRoomRepository.findBySenderIdAndRecipientId(recipientId,senderId);

        String chatId = firstChatRoom.map(ChatRoom::getChatId).orElseThrow(()-> new ChatRoomException("Doesnt exist chatroom between users!"));

        firstChatRoom.ifPresent(chatRoomRepository::delete);
        secondChatRoom.ifPresent(chatRoomRepository::delete);

        deleteChatRoomMessages(chatId);
    }

    private void deleteChatRoomMessages(String chatId){
        List<ChatMessage> roomMessages = chatMessageRepository.findByChatId(chatId);

        chatMessageRepository.deleteAll(roomMessages);
    }
}
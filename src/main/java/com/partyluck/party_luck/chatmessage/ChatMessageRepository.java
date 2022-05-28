package com.partyluck.party_luck.chatmessage;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findChatMessagesByChatroom_ChatRoomIdOrderByCreatedAt(String chatroomId);
    void deleteAllByChatroom_ChatRoomId(String chatRoomId);
}

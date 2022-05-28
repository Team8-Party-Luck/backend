package com.partyluck.party_luck.chatmessage;



import com.partyluck.party_luck.chatmessage.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findChatMessagesByChatroom_ChatRoomIdOrderByCreatedAt(String chatroomId);
    List<ChatMessage> deleteAllByChatroom_ChatRoomId(String chatRoomId);
}

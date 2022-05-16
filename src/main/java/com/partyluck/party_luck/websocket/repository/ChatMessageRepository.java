package com.partyluck.party_luck.websocket.repository;



import com.partyluck.party_luck.websocket.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findChatMessagesByChatroom_ChatRoomIdOrderByCreatedAt(String chatroomId);
}

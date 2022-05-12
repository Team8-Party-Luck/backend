package com.partyluck.party_luck.websocket.repository;

import com.partyluck.party_luck.websocket.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}

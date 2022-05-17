package com.partyluck.party_luck.websocket.repository;



import com.partyluck.party_luck.websocket.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByChatRoomId(String chatRoomId);
}

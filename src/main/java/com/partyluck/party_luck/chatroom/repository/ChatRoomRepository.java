package com.partyluck.party_luck.chatroom.repository;



import com.partyluck.party_luck.chatroom.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByChatRoomId(String chatRoomId);
    Optional<ChatRoom> deleteByChatRoomId(String chatRoomId);
}

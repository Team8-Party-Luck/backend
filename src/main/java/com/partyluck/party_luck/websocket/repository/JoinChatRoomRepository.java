package com.partyluck.party_luck.websocket.repository;



import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.websocket.domain.JoinChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinChatRoomRepository extends JpaRepository<JoinChatRoom, Long> {
    List<JoinChatRoom> findJoinChatRoomsByUser(User user);
    List<JoinChatRoom> findJoinChatRoomsByChatRoom_ChatRoomId(String chatRoomId);
    List<JoinChatRoom> findJoinChatRoomsByUser_Id(Long userId);
}

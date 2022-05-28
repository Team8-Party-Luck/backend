package com.partyluck.party_luck.chatroom.repository;



import com.partyluck.party_luck.user.domain.User;
import com.partyluck.party_luck.chatroom.domain.JoinChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinChatRoomRepository extends JpaRepository<JoinChatRoom, Long> {
    List<JoinChatRoom> findJoinChatRoomsByUser(User user);
    List<JoinChatRoom> findJoinChatRoomsByChatRoom_ChatRoomId(String chatRoomId);
    List<JoinChatRoom> findJoinChatRoomsByUser_Id(Long userId);
    void deleteAllByUser(User user);
}

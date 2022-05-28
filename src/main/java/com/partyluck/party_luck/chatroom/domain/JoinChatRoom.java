package com.partyluck.party_luck.chatroom.domain;

import com.partyluck.party_luck.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class JoinChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private ChatRoom chatRoom;

    public JoinChatRoom(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }
}

package com.partyluck.party_luck.websocket.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
@Entity
public class ChatRoom {
    @Id
    private String chatRoomId;

    @Column
    private Long partyId;

    public ChatRoom(Long partyId) {
        this.chatRoomId = UUID.randomUUID().toString();
        this.partyId = partyId;
    }

}

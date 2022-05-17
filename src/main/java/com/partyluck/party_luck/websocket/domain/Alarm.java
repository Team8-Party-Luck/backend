package com.partyluck.party_luck.websocket.domain;

import com.partyluck.party_luck.websocket.dto.AlarmDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private Long partyId;

    private String type;

    private Long userId;
    public Alarm(AlarmDto dto){
        this.partyId=dto.getPartyId();
        this.type=dto.getType();
        this.userId=dto.getUserId();
    }


}

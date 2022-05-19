package com.partyluck.party_luck.websocket.domain;

import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.websocket.Timestamped;
import com.partyluck.party_luck.websocket.dto.reponse.AlarmPageResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    @Column
    private String alarmMessage;

    @Column
    private Long partyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column
    private String createdAt;

//    public Alarm(AlarmDto dto){
//        this.partyId=dto.getPartyId();
//        this.type=dto.getType();
//        this.userId=dto.getUserId();
//    }


    public Alarm(AlarmPageResponseDto alarmPageResponseDto, Long id, User user, String createdAt) {

        this.alarmMessage =alarmPageResponseDto.getAlarms();
        this.partyId = id;
        this.user = user;
        this.createdAt = createdAt;
    }
}

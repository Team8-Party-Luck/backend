package com.partyluck.party_luck.alarm;

import com.partyluck.party_luck.user.domain.User;
import com.partyluck.party_luck.alarm.responseDto.AlarmPageResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    @Column
    private String alarms;

    @Column
    private Long partyId;

    @Column
    private String title;

    @Column
    private String store;

    @Column
    private String image;

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

        this.alarms = alarmPageResponseDto.getAlarms();
        this.partyId = id;
        this.title = alarmPageResponseDto.getTitle();
        this.store = alarmPageResponseDto.getStore();
        this.image = alarmPageResponseDto.getImage();
        this.user = user;
        this.createdAt = createdAt;
    }
}

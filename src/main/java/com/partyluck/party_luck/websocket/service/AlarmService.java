package com.partyluck.party_luck.websocket.service;

import com.partyluck.party_luck.dto.party.request.PartyRequestDto;
import com.partyluck.party_luck.repository.ImageRepository;
import com.partyluck.party_luck.repository.PartyJoinRepository;
import com.partyluck.party_luck.repository.PartyRepository;
import com.partyluck.party_luck.websocket.domain.Alarm;
import com.partyluck.party_luck.websocket.dto.reponse.AlarmPageResponseDto;
import com.partyluck.party_luck.websocket.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final PartyRepository partyRepository;
    private final ImageRepository imageRepository;

    // 알람 메시지 전체 조회
    public List<AlarmPageResponseDto> getAlarm(Long userId) {

        List<Alarm> alarmList = alarmRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        List<AlarmPageResponseDto> alarmPageResponseDtoList = new ArrayList<>();


        for (Alarm alarm : alarmList) {

            String image = imageRepository.findImageByImgIndexAndPartyid(1, alarm.getPartyId()).get().getImageSrc();
            String title = partyRepository.findById(alarm.getPartyId()).get().getTitle();
            String store = partyRepository.findById(alarm.getPartyId()).get().getStore();
            String alarms=alarm.getAlarmMessage();
            String curtime = alarm.getCreatedAt();


            AlarmPageResponseDto alarmPageResponseDto = new AlarmPageResponseDto(title, store, image, alarms,curtime);
            alarmPageResponseDtoList.add(alarmPageResponseDto);
        }
        return alarmPageResponseDtoList;
    }

    // 알람 메시지 삭제
    public void deleteAlarm(Long alarmId) {

        alarmRepository.deleteById(alarmId);
    }

}



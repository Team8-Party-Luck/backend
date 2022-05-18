package com.partyluck.party_luck.websocket.controller;

import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.websocket.dto.reponse.AlarmPageResponseDto;
import com.partyluck.party_luck.websocket.repository.AlarmRepository;
import com.partyluck.party_luck.websocket.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final AlarmRepository alarmRepository;

    //알람 메시지 조회
    @GetMapping("/alarmList")
    public List<AlarmPageResponseDto> getAlarm(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getId();
        List<AlarmPageResponseDto> alarmPageResponseDtoList = alarmService.getAlarm(userId);

        return alarmPageResponseDtoList;
    }

    //알람 메시지 삭제
    @DeleteMapping("/delete/alarm/{alarmId}")
    public Long deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarm(alarmId);

        return alarmId;
    }

}



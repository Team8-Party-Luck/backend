package com.partyluck.party_luck.alarm;

import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.alarm.responseDto.AlarmPageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

//    @PutMapping("/alarm/check")
//    public String checkAlarm(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//
//    }

    //알람 메시지 삭제
    @DeleteMapping("/delete/alarm/{alarmId}")
    public Long deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarm(alarmId);

        return alarmId;
    }

}



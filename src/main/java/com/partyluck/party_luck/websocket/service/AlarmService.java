package com.partyluck.party_luck.websocket.service;

import com.partyluck.party_luck.domain.Party;
import com.partyluck.party_luck.domain.PartyJoin;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.ImageRepository;
import com.partyluck.party_luck.repository.PartyJoinRepository;
import com.partyluck.party_luck.repository.PartyRepository;
import com.partyluck.party_luck.websocket.domain.Alarm;
import com.partyluck.party_luck.websocket.dto.reponse.AlarmPageResponseDto;
import com.partyluck.party_luck.websocket.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final PartyRepository partyRepository;
    private final ImageRepository imageRepository;
    private final PartyJoinRepository partyJoinRepository;
    private final SimpMessageSendingOperations messagingTemplate;


    // 파티 하루 전 알림
    public void sendAlarm(Long partyId) throws ParseException {
        System.out.println("알림 시작 ---------------------");
        //조인한 파티 D-Day
        Party foundParty = partyRepository.findById(partyId).orElseThrow(
                () -> new IllegalArgumentException("해당 파티가 존재하지 않습니다.")
        );

        String month = foundParty.getDate().split("-")[0];
        String day = foundParty.getDate().split("-")[1];
        String hour = foundParty.getTime().split(":")[0];
        String minute = foundParty.getTime().split(":")[1];
        String dDay = month + day + hour + minute;
        System.out.println("dDay : " + dDay);

        //String 에서 Date 타입으로 변환
        SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmm");
        Date dDayTime = formatter.parse(dDay);
        System.out.println("dDay Date 변환 : " + dDayTime);


        System.out.println("캘린더 객체 호출 전-------------------");
        //두시간 전
        Calendar preTwoHours = Calendar.getInstance();
        System.out.println("setTime 호출 전 ");
        preTwoHours.setTime(dDayTime);
        System.out.println("setTime 호출 후 ");
        System.out.println(preTwoHours.getTime());

        preTwoHours.add(Calendar.HOUR, -2);
        Date twoHours = preTwoHours.getTime();
        System.out.println("두시간전 캘린더 객체 생성 : " + preTwoHours);

        //하루 전
        Calendar preOneDay = Calendar.getInstance();
        preOneDay.setTime(dDayTime);
        preOneDay.add(Calendar.DATE, -1);
        Date oneDay = preOneDay.getTime();
        System.out.println("하루전 캘린더 객체 생성 : " + preTwoHours);

        Timer timer = new Timer();

        //알람에 들어갈 내용
        String image = imageRepository.findImageByImgIndexAndPartyid(1, partyId).get().getImageSrc();
        String title = partyRepository.findById(partyId).get().getTitle();
        String store = partyRepository.findById(partyId).get().getStore();

        SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
        Date cur = new Date();
        String curtime = format1.format(cur);

        //하루 전 task 실행
        TimerTask oneDayAlarm = new TimerTask() {
            public void run() {
                System.out.println("하루전 타이머생성");
                //알람 텍스트 설정
                String alarms = "신청하신 파티 하루 전입니다 -----------------------------------";

                //알람보내기 - 참여한 파티 구성원들에게 다 보내주기
                List<PartyJoin> tmp = partyJoinRepository.findAllByParty(partyRepository.findById(partyId).orElse(null));
                for (PartyJoin p : tmp) {
                    User user = p.getUser();
                    AlarmPageResponseDto alarmPageResponseDto = new AlarmPageResponseDto(image, title, store, alarms, curtime);
                    Alarm alarm = new Alarm(alarmPageResponseDto, partyId, user, curtime);
                    alarmRepository.save(alarm);
                    System.out.println("alarm save");
                    messagingTemplate.convertAndSend("/alarm/" + user.getId().toString(), alarmPageResponseDto);
                }
            }
        };

        //두시간 전 task 실행
        TimerTask twoHoursAlarm = new TimerTask() {
            public void run() {
                System.out.println("두시간전 타이머생성 -----------------------------------");
                String alarms = "신청하신 파티 두 시간 전입니다";

                //알람보내기 - 참여한 파티 구성원들에게 다 보내주기
                List<PartyJoin> tmp = partyJoinRepository.findAllByParty(partyRepository.findById(partyId).orElse(null));
                for (PartyJoin p : tmp) {
                    User user = p.getUser();
                    AlarmPageResponseDto alarmPageResponseDto = new AlarmPageResponseDto(image, title, store, alarms, curtime);
                    Alarm alarm = new Alarm(alarmPageResponseDto, partyId, user, curtime);
                    alarmRepository.save(alarm);
                    messagingTemplate.convertAndSend("/alarm/" + user.getId().toString(), alarmPageResponseDto);
                }
            }
        };
        System.out.println("스케쥴러 등록 -----------------------------------");
        timer.schedule(oneDayAlarm, oneDay);
        timer.schedule(twoHoursAlarm, twoHours);

    }


    // 알람 메시지 전체 조회
    public List<AlarmPageResponseDto> getAlarm(Long userId) {

        List<Alarm> alarmList = alarmRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        List<AlarmPageResponseDto> alarmPageResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmList) {

            String image = alarm.getImage();
            String title = alarm.getTitle();
            String store = alarm.getStore();
            String alarms = alarm.getAlarms();
            String curtime = alarm.getCreatedAt();


            AlarmPageResponseDto alarmPageResponseDto = new AlarmPageResponseDto(title, store, image, alarms, curtime);
            alarmPageResponseDtoList.add(alarmPageResponseDto);
        }
        return alarmPageResponseDtoList;
    }

    // 알람 메시지 삭제
    public void deleteAlarm(Long alarmId) {

        alarmRepository.deleteById(alarmId);
    }

}



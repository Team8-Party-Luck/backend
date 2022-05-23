package com.partyluck.party_luck.websocket.service;

import com.partyluck.party_luck.domain.Party;
import com.partyluck.party_luck.domain.PartyJoin;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.ImageRepository;
import com.partyluck.party_luck.repository.PartyJoinRepository;
import com.partyluck.party_luck.repository.PartyRepository;
import com.partyluck.party_luck.service.party.PartyService;
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
    private final PartyService partyService;


    // 파티 하루 전 알림
    public void sendAlarm(Long partyId) throws ParseException {
        //조인한 파티 D-Day
        Party foundParty = partyRepository.findById(partyId).orElseThrow(
                () -> new IllegalArgumentException("해당 파티가 존재하지 않습니다.")
        );

        String month = foundParty.getDate().split("-")[0];
        String day = foundParty.getDate().split("-")[1];
        String hour = foundParty.getTime().split(":")[0];
        String minute = foundParty.getTime().split(":")[1];
        String dDay = "2022" + month + day + hour + minute;

        //String 에서 Date 타입으로 변환
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        Date dDayTime = formatter.parse(dDay);

        //하루 전
        Calendar preOneDay = Calendar.getInstance();
        preOneDay.setTime(dDayTime);
        preOneDay.add(Calendar.DATE, -1);
        Date oneDay = preOneDay.getTime();

        //두시간 전
        Calendar preTwoHours = Calendar.getInstance();
        preTwoHours.setTime(dDayTime);
        preTwoHours.add(Calendar.HOUR, -2);
        Date twoHours = preTwoHours.getTime();

        //알람에 들어갈 내용
//        }

            String image = imageRepository.findImageByImgIndexAndPartyid(1, partyId).get().getImageSrc();
            String title = partyRepository.findById(partyId).get().getTitle();
            String store = partyRepository.findById(partyId).get().getStore();

            SimpleDateFormat format1 = new SimpleDateFormat("MMddHHmm");
            Date cur = new Date();
            String curtime = format1.format(cur);


            Timer timer = new Timer();

            //하루 전 task 실행
            TimerTask oneDayAlarm = new TimerTask() {
                public void run() {
                    System.out.println("하루전 타이머생성");
                    //알람 텍스트 설정
                    String alarms = "신청하신 파티 하루 전입니다 -----------------------------------";

                    if(partyRepository.findById(partyId).orElse(null)!= null) {
                        //알람보내기 - 참여한 파티 구성원들에게 다 보내주기
                        List<PartyJoin> tmp = partyJoinRepository.findAllByParty(partyRepository.findById(partyId).orElse(null));
                        for (PartyJoin p : tmp) {
                            User user = p.getUser();
                            AlarmPageResponseDto alarmPageResponseDto = new AlarmPageResponseDto(image, title, store, alarms, curtime);
                            Alarm alarm = new Alarm(alarmPageResponseDto, partyId, user, curtime);
                            alarmRepository.save(alarm);
                            System.out.println("alarm save");
                            messagingTemplate.convertAndSend("/alarm/" + user.getId().toString(), alarmPageResponseDto);
                            timer.cancel();
                        }
                    } else {
                        timer.cancel();
                    }
                }
            };

            //두시간 전 task 실행
            TimerTask twoHoursAlarm = new TimerTask() {
                public void run() {
                    System.out.println("두시간전 타이머생성 -----------------------------------");
                    String alarms = "신청하신 파티 두 시간 전입니다";

                    //알람보내기 - 참여한 파티 구성원들에게 다 보내주기
                    if(partyRepository.findById(partyId).orElse(null)!= null) {
                        List<PartyJoin> tmp = partyJoinRepository.findAllByParty(partyRepository.findById(partyId).orElse(null));
                        for (PartyJoin p : tmp) {
                            User user = p.getUser();
                            AlarmPageResponseDto alarmPageResponseDto = new AlarmPageResponseDto(image, title, store, alarms, curtime);
                            Alarm alarm = new Alarm(alarmPageResponseDto, partyId, user, curtime);
                            alarmRepository.save(alarm);
                            messagingTemplate.convertAndSend("/alarm/" + user.getId().toString(), alarmPageResponseDto);
                            timer.cancel();
                        }
                    } else {
                        timer.cancel();
                    }
                }
            };

            System.out.println("알람 등록 -----------------------------------");
            Date now = new Date();
            //등록 시점이 (만나는 날짜- 하루)보다 전에 있을 때
            if (now.before(oneDay)) {
                timer.schedule(oneDayAlarm, oneDay);
                timer.schedule(twoHoursAlarm, twoHours);
                //등록 시점이 (만나는 날짜 -하루)보다 뒤에 있고 (만나는 날짜 - 2시간)보다 전에 있을 때
            } else if (now.before(twoHours) && now.after(oneDay)) {
                timer.schedule(twoHoursAlarm, twoHours);
                //등록 시점이 (만나는 날짜 -2시간)보다 뒤에 있을
            } else if (now.after(twoHours)) {
                timer.cancel();
            }

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
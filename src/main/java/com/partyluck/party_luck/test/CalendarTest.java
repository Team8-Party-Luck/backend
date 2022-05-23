package com.partyluck.party_luck.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarTest {
    public static void main(String[] args) throws ParseException {

        String dDay = "2022"+"05230303";
        System.out.println("dDay : " + dDay);

        //String 에서 Date 타입으로 변환

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        Date dDayTime = formatter.parse(dDay);
        System.out.println("dDay Date 변환 : " + dDayTime);

        Calendar preTwoHours = Calendar.getInstance();
        preTwoHours.setTime(dDayTime);
        System.out.println(preTwoHours.getTime());
        preTwoHours.add(Calendar.HOUR, -2);
        System.out.println(preTwoHours.getTime());
        System.out.println(preTwoHours.getTime().getClass().getName());

        Timer timer = new Timer();

        //두시간 전 task 실행
        TimerTask twoHoursAlarm = new TimerTask() {
            @Override
            public void run() {
                System.out.println("두시간전 타이머생성 -----------------------------------");
                timer.cancel();
            }
        };

        Date now = new Date();
//        Calendar nowSubTwo = Calendar.getInstance();
//        nowSubTwo.setTime(now);
//        nowSubTwo.add(Calendar.HOUR, -2);
//        System.out.println(nowSubTwo.getTime());
        if(now.before(preTwoHours.getTime())) {
            timer.schedule(twoHoursAlarm, preTwoHours.getTime());
        } else {
            System.out.println("알림 필요없음");
            timer.cancel();
        }
    }
}

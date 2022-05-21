package com.partyluck.party_luck.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class CalendarTest {
    public static void main(String[] args) throws ParseException {

        String dDay = "03151730";
        System.out.println("dDay : " + dDay);

        //String 에서 Date 타입으로 변환
        SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmm");
        Date dDayTime = formatter.parse(dDay);
        System.out.println("dDay Date 변환 : " + dDayTime);

        Calendar preTwoHours = Calendar.getInstance();
        preTwoHours.setTime(dDayTime);
        System.out.println(preTwoHours.getTime());
        preTwoHours.add(Calendar.HOUR, -2);
        System.out.println(preTwoHours.getTime());
        System.out.println(preTwoHours.getTime().getClass().getName());

        Timer timer = new Timer();


    }
}

//package com.partyluck.party_luck.test;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//
//public class DateFormTest {
//
//    public static void main(String[] args) {
//        List<String> dateList = new ArrayList<>();
//        dateList.add("2022-05-20 03:12");
//        dateList.add("2022-05-20 12:12");
//        dateList.add("2022-05-19 12:12");
//        dateList.add("2022-06-20 14:22");
//        dateList.add("2022-05-19 15:00");
//        dateList.add("2022-06-20 05:00");
//        dateList.add("2022-05-20 12:25");
//
//        for(String date : dateList) {
//            /* 날짜형식 로직
//            1. 오늘인지 확인
//            2. 오전인지 오후인지 확인 오전 02시:30분 or 오후 03:00
//            3. 하루전이면 하루전
//            4. 하루전 보다 길면
//         */
//            System.out.println(extractDateFormat(date));
//        }
//    }
//
//    public static String extractDateFormat(String messageDate) {
//        String result = "";
//
//        Date now = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String formattedNow = simpleDateFormat.format(now).toString();
//        String[] curFormattedSplitByDash = formattedNow.split("-");
//        String curYear = curFormattedSplitByDash[0];
//        String curMonth = curFormattedSplitByDash[1];
//        String curDay = curFormattedSplitByDash[2].split(" ")[0];
//        String curHour = curFormattedSplitByDash[2].split(" ")[1].split(":")[0];
//        String curMinute = curFormattedSplitByDash[2].split(" ")[1].split(":")[1];
//        System.out.println("----------------- 현재 날짜 & 시각 -----------------");
//        System.out.println(curYear + "년 " + curMonth + "월 " + curDay + "일 " + curHour + "시 " + curMinute + "분 ");
//
//        String[] formattedSplitByDash = messageDate.split("-");
//        String year = formattedSplitByDash[0];
//        String month = formattedSplitByDash[1];
//        String day = formattedSplitByDash[2].split(" ")[0];
//        String hour = formattedSplitByDash[2].split(" ")[1].split(":")[0];
//        String minute = formattedSplitByDash[2].split(" ")[1].split(":")[1];
//
//        // 1) 오늘인지 아닌지
//        if(curMonth.equals(month) && curDay.equals(day)) {
//            // 2) 오전인지 오후 인지
//            if(Integer.parseInt(hour) < 12) {
//                result = "오전 " + hour + ":" + minute;
//            } else {
//                Integer afterHour = (Integer.parseInt(hour) - 12);
//                result = "오후 " + afterHour.toString() + ":" + minute;
//            }
//        } else if (curMonth.equals(month) && ((Integer.parseInt(curDay) - 1) == Integer.parseInt(day))) {
//            // 3) 하루 전인지 아닌지
//            result = "하루 전";
//        } else {
//            // 4) 하루 전이 아니라면
//            result = month + "월 " + day + "일";
//        }
//        return result;
//    }
//}

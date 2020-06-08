package com.jd.platform.hotkey.dashboard.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author liyunfeng31
 */
public class DateUtil {


    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter TIME_FORMAT1 = DateTimeFormatter.ofPattern("yyMMddHHmm");

    public static final DateTimeFormatter TIME_FORMAT2 = DateTimeFormatter.ofPattern("yyMMddHH");

    public static final DateTimeFormatter TIME_FORMAT3 = DateTimeFormatter.ofPattern("yyMMddHH");

    public static Date strToDate(String str){
        try {
            return simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static int nowMinus(LocalDateTime now){
        return Integer.parseInt(now.format(TIME_FORMAT1)) ;
    }

    public static int nowHour(LocalDateTime now){
        return Integer.parseInt(now.format(TIME_FORMAT2));
    }

    public static int nowDay(LocalDateTime now){ return Integer.parseInt(now.format(TIME_FORMAT3));}


    public static int preHours(LocalDateTime now, int hours){
        return Integer.parseInt(now.minusHours(hours).format(TIME_FORMAT2));
    }

    public static Date preTime(int hours){
       return localDateTimeToDate(LocalDateTime.now().minusHours(hours));
    }


    public static Date preMinus(int minus){
        return localDateTimeToDate(LocalDateTime.now().minusMinutes(minus));
    }

    public static Date preDays(int days){
        return localDateTimeToDate(LocalDateTime.now().minusDays(days));
    }
}

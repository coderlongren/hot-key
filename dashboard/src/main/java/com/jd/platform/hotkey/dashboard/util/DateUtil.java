package com.jd.platform.hotkey.dashboard.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author liyunfeng31
 */
public class DateUtil {

    public static LocalDateTime dateToLocalDateTime(Date date){
        return date.toInstant().atZone( ZoneId.systemDefault()).toLocalDateTime();
    }


    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

package com.hdshop.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeUtils {
    public static Date getCurrentDateTime(Date date) {
        // TODO Config to date time for Ho Chi Minh city
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        ZonedDateTime utcDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime hoChiMinhDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"));
        return Date.from(hoChiMinhDateTime.toInstant());
    }
}

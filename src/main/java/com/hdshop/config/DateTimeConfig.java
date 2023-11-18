package com.hdshop.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeConfig {
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withZone(ZONE_ID);

    public static Date getCurrentDateTimeInTimeZone() {
        return Date.from(ZonedDateTime.now(ZONE_ID).toInstant());
    }

    public static String formatDateTime(Date date) {
        return DATE_TIME_FORMATTER.format(date.toInstant());
    }

    public static Date parseDateTime(String dateTime) {
        return Date.from(ZonedDateTime.parse(dateTime, DATE_TIME_FORMATTER).toInstant());
    }
}

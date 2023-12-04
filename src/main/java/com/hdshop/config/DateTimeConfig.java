package com.hdshop.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class   DateTimeConfig {
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(ZONE_ID);

    public static ZonedDateTime getCurrentDateTimeInTimeZone() {
        return ZonedDateTime.now(ZONE_ID);
    }

    public static String formatDateTime(ZonedDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    public static ZonedDateTime parseDateTime(String dateTime) {
        if (dateTime.length() == 10) { // Kiểm tra độ dài chuỗi, nếu là "dd/MM/yyyy"
            return ZonedDateTime.parse(dateTime + " 00:00:00", DATE_TIME_FORMATTER);
        } else { // Nếu là "dd/MM/yyyy HH:mm:ss"
            return ZonedDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        }
    }
}

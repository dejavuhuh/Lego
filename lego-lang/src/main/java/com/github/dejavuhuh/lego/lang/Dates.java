package com.github.dejavuhuh.lego.lang;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具类
 *
 * @author wu.yue
 * @since 2023/12/30 13:49
 */
public class Dates {

    static String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static Map<String, DateTimeFormatter> formatters =
            new HashMap<String, DateTimeFormatter>() {
                {
                    put(DEFAULT_PATTERN, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    put("yyyyMMddHHmmss", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                }
            };

    public static String format(TemporalAccessor date, String pattern) {
        DateTimeFormatter formatter =
                formatters.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
        return formatter.format(date);
    }

    public static String format(TemporalAccessor date) {
        return format(date, DEFAULT_PATTERN);
    }

    public static String formatNow(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    public static String formatNow() {
        return formatNow(DEFAULT_PATTERN);
    }
}

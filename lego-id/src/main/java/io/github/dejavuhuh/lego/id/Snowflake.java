package io.github.dejavuhuh.lego.id;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 雪花算法
 *
 * @author wu.yue
 * @since 2024/1/11 13:54
 */
public class Snowflake {

    static long MAX_NEXT = 0b11111_11111111_111L;
    static long OFFSET = LocalDate.of(2000, 1, 1).atStartOfDay(ZoneId.of("Z")).toEpochSecond();
    long workerId;
    long offset = 0;
    long lastEpoch = 0;

    public Snowflake(long workerId) {
        if (workerId < 0) {
            throw new IllegalArgumentException("workerId 不能为负数");
        }
        this.workerId = workerId;
    }

    public long nextId() {
        return nextId(System.currentTimeMillis() / 1000);
    }

    private synchronized long nextId(long epochSecond) {
        if (epochSecond < lastEpoch) {
            epochSecond = lastEpoch;
        }
        if (lastEpoch != epochSecond) {
            lastEpoch = epochSecond;
            reset();
        }
        offset++;
        long next = offset & MAX_NEXT;
        if (next == 0) {
            return nextId(epochSecond + 1);
        }
        return generateId(epochSecond, next, workerId);
    }

    private void reset() {
        offset = 0;
    }

    private long generateId(long epochSecond, long next, long workerId) {
        return ((epochSecond - OFFSET) << 21) | (next << 5) | workerId;
    }
}

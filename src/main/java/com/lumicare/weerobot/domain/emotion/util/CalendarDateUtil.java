package com.lumicare.weerobot.domain.emotion.util;

import java.time.LocalDateTime;

public class CalendarDateUtil {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public CalendarDateUtil(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}

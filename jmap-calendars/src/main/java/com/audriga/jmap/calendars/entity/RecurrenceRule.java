package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import java.time.LocalDateTime;
import java.util.List;
import org.jspecify.annotations.Nullable;

@Type
public record RecurrenceRule(
        String frequency,
        @Default("1") Long interval,
        @Default("\"gregorian\"") String rscale,
        @Default("\"omit\"") String skip,
        @Default("\"mo\"") String firstDayOfWeek,
        @Nullable List<NDay> byDay,
        @Nullable List<Integer> byMonthDay,
        @Nullable List<String> byMonth,
        @Nullable List<Integer> byYearDay,
        @Nullable List<Integer> byWeekNo,
        @Nullable List<Integer> byHour,
        @Nullable List<Integer> byMinute,
        @Nullable List<Integer> bySecond,
        @Nullable List<Long> bySetPosition,
        @Nullable Long count,
        @Nullable LocalDateTime until) {
    @Type
    public record NDay(String day, @Nullable Long nthOfPeriod) {}
}

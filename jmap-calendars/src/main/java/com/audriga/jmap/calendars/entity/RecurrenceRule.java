package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record RecurrenceRule(
        Frequency frequency,
        @Default("1") Long interval,
        @Default("\"gregorian\"") String rscale,
        @Default("\"omit\"") String skip,
        @Default("\"mo\"") WeekDay firstDayOfWeek,
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
    public enum Frequency {
        yearly,
        monthly,
        weekly,
        daily,
        hourly,
        minutely,
        secondly,
    }

    @Type
    public record NDay(WeekDay day, @Nullable Long nthOfPeriod) {}

    public enum WeekDay {
        mo,
        tu,
        we,
        th,
        fr,
        sa,
        su,
    }
}

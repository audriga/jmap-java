package com.audriga.jmap.common;

import java.time.Duration;
import java.time.Period;

/**
 * Date-time period, stored as an {@link Period} and {@link Duration}.
 * If negative, both {@code period} and {@code duration} will be less than or equal to zero.
 *
 * @param period   date period
 * @param duration time duration
 */
public record DateTimePeriod(Period period, Duration duration) {
    public static DateTimePeriod parse(String iso) {
        var parts = iso.split("T", 2);
        var period = parts[0].endsWith("P") ? Period.ZERO : Period.parse(parts[0]);
        var duration = parts.length == 2 ? Duration.parse("PT" + parts[1]) : Duration.ZERO;
        return new DateTimePeriod(period, parts[0].startsWith("-") ? duration.negated() : duration);
    }

    @Override
    public String toString() {
        return period.toString() + duration.abs().toString().substring(1);
    }
}

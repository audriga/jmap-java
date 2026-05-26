package com.audriga.jmap.common;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;

/**
 * Date-time period, stored as an {@link Period} and {@link Duration}.
 * If negative, both {@code period} and {@code duration} will be less than or equal to zero.
 *
 * @param period   date period
 * @param duration time duration
 */
public record DateTimePeriod(Period period, Duration duration) implements TemporalAmount {
    private static final List<TemporalUnit> SUPPORTED_UNITS =
            List.of(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS, ChronoUnit.SECONDS, ChronoUnit.NANOS);

    public static DateTimePeriod parse(String iso) {
        var parts = iso.split("T", 2);
        var period = parts[0].endsWith("P") ? Period.ZERO : Period.parse(parts[0]);
        var duration = parts.length == 2 ? Duration.parse("PT" + parts[1]) : Duration.ZERO;
        return new DateTimePeriod(period, parts[0].startsWith("-") ? duration.negated() : duration);
    }

    public DateTimePeriod abs() {
        return new DateTimePeriod(period.isNegative() ? period.negated() : period, duration.abs());
    }

    public DateTimePeriod plus(DateTimePeriod other) {
        return new DateTimePeriod(period.plus(other.period), duration.plus(other.duration));
    }

    public DateTimePeriod minus(DateTimePeriod other) {
        return new DateTimePeriod(period.minus(other.period), duration.minus(other.duration));
    }

    @Override
    public long get(TemporalUnit unit) {
        if (period.getUnits().contains(unit)) {
            return period.get(unit);
        } else if (duration.getUnits().contains(unit)) {
            return duration.get(unit);
        } else {
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return SUPPORTED_UNITS;
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return temporal.plus(period).plus(duration);
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return temporal.minus(period).minus(duration);
    }

    @Override
    public String toString() {
        return period.toString() + duration.abs().toString().substring(1);
    }
}

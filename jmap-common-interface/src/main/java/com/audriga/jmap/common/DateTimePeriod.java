package com.audriga.jmap.common;

import java.time.Duration;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
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
public record DateTimePeriod(Period period, Duration duration) implements TemporalAmount, ChronoPeriod {
    private static final List<TemporalUnit> SUPPORTED_UNITS =
            List.of(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS, ChronoUnit.SECONDS, ChronoUnit.NANOS);

    public DateTimePeriod {
        if (period.isNegative() && !(duration.isNegative() || duration.isZero())) {
            throw new IllegalArgumentException("period and duration may not be a mix of positive and negative");
        }
    }

    public static DateTimePeriod of(Period period) {
        return new DateTimePeriod(period, Duration.ZERO);
    }

    public static DateTimePeriod of(Duration duration) {
        return new DateTimePeriod(Period.ZERO, duration);
    }

    public static DateTimePeriod parse(String iso) {
        var parts = iso.split("T", 2);
        var period = parts[0].endsWith("P") ? Period.ZERO : Period.parse(parts[0]);
        var duration = parts.length == 2 ? Duration.parse("PT" + parts[1]) : Duration.ZERO;
        return new DateTimePeriod(period, parts[0].startsWith("-") ? duration.negated() : duration);
    }

    public DateTimePeriod abs() {
        return new DateTimePeriod(period.isNegative() ? period.negated() : period, duration.abs());
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
    public Chronology getChronology() {
        return period.getChronology();
    }

    @Override
    public DateTimePeriod plus(TemporalAmount amountToAdd) {
        var resultPeriod = period;
        var resultDuration = duration;
        for (var unit : amountToAdd.getUnits()) {
            var value = amountToAdd.get(unit);
            if (unit == ChronoUnit.YEARS) {
                resultPeriod = resultPeriod.plusYears(value);
            } else if (unit == ChronoUnit.MONTHS) {
                resultPeriod = resultPeriod.plusMonths(value);
            } else if (unit == ChronoUnit.DAYS) {
                resultPeriod = resultPeriod.plusDays(value);
            } else {
                resultDuration = resultDuration.plus(value, unit);
            }
        }
        return new DateTimePeriod(resultPeriod, resultDuration);
    }

    @Override
    public DateTimePeriod minus(TemporalAmount amountToSubtract) {
        var resultPeriod = period;
        var resultDuration = duration;
        for (var unit : amountToSubtract.getUnits()) {
            var value = amountToSubtract.get(unit);
            if (unit == ChronoUnit.YEARS) {
                resultPeriod = resultPeriod.minusYears(value);
            } else if (unit == ChronoUnit.MONTHS) {
                resultPeriod = resultPeriod.minusMonths(value);
            } else if (unit == ChronoUnit.DAYS) {
                resultPeriod = resultPeriod.minusDays(value);
            } else {
                resultDuration = resultDuration.minus(value, unit);
            }
        }
        return new DateTimePeriod(resultPeriod, resultDuration);
    }

    @Override
    public ChronoPeriod multipliedBy(int scalar) {
        return new DateTimePeriod(period.multipliedBy(scalar), duration.multipliedBy(scalar));
    }

    @Override
    public ChronoPeriod normalized() {
        var normalizedPeriod = period.normalized();
        return normalizedPeriod == period ? this : new DateTimePeriod(normalizedPeriod, duration);
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

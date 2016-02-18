package pl.pamsoft.imapcloud.mbeans;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Immutable
@RequiredArgsConstructor
@Getter
public class StatsContainer {
	private final long min;
	private final long max;
	private final double avg;

	@Override
	public String toString() {
		return "{" +
			"min=" + toPrettyString(min) +
			", max=" + toPrettyString(max) +
			", avg=" + toPrettyString((long) avg) +
			'}';
	}

	/**
	 * Copied from com.google.common.base.Stopwatch#toString()
     */
	private String toPrettyString(long value) {
		TimeUnit unit = chooseUnit(value);
		double val = (double) value / NANOSECONDS.convert(1, unit);

		// Too bad this functionality is not exposed as a regular method call
		return String.format(Locale.ROOT, "%.4g %s", val, abbreviate(unit));
	}

	private static TimeUnit chooseUnit(long nanos) {
		if (DAYS.convert(nanos, NANOSECONDS) > 0) {
			return DAYS;
		}
		if (HOURS.convert(nanos, NANOSECONDS) > 0) {
			return HOURS;
		}
		if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
			return MINUTES;
		}
		if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
			return SECONDS;
		}
		if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
			return MILLISECONDS;
		}
		if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
			return MICROSECONDS;
		}
		return NANOSECONDS;
	}

	private static String abbreviate(TimeUnit unit) {
		switch (unit) {
			case NANOSECONDS:
				return "ns";
			case MICROSECONDS:
				return "\u03bcs"; // μs
			case MILLISECONDS:
				return "ms";
			case SECONDS:
				return "s";
			case MINUTES:
				return "min";
			case HOURS:
				return "h";
			case DAYS:
				return "d";
			default:
				throw new AssertionError();
		}
	}
}

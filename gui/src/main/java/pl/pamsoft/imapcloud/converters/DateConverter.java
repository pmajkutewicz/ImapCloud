package pl.pamsoft.imapcloud.converters;

import javafx.util.StringConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateConverter extends StringConverter<Double> {

	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
		.appendValue(ChronoField.HOUR_OF_DAY).appendLiteral(':')
		.appendValue(ChronoField.MINUTE_OF_HOUR).appendLiteral(':')
		.appendValue(ChronoField.SECOND_OF_MINUTE)
		.toFormatter();

	@Override
	public String toString(Double object) {
		LocalDateTime localDateTime = toLocalDateTime(object.longValue());
		return FORMATTER.format(localDateTime);
	}

	@Override
	public Double fromString(String string) {
		LocalDateTime ldt = LocalDateTime.parse(string, FORMATTER);
		ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
		return (double)zdt.toInstant().toEpochMilli();
	}

	private LocalDateTime toLocalDateTime(long timestamp) {
		return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

}

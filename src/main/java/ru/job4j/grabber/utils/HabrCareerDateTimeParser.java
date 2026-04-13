package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class HabrCareerDateTimeParser implements DateTimeParser {
    private static final String TODAY_WORD = "\u0441\u0435\u0433\u043e\u0434\u043d\u044f";
    private static final String YESTERDAY_WORD = "\u0432\u0447\u0435\u0440\u0430";
    private static final Locale RU = new Locale("ru");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_WITH_TIME = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", RU);
    private static final DateTimeFormatter DATE_WITHOUT_YEAR = DateTimeFormatter.ofPattern("d MMMM, HH:mm", RU);

    @Override
    public LocalDateTime parse(String parse) {
        String source = parse.toLowerCase(RU).trim();
        if (source.contains(TODAY_WORD)) {
            LocalTime time = LocalTime.parse(source.substring(source.length() - 5), TIME_FORMATTER);
            return LocalDate.now().atTime(time);
        }
        if (source.contains(YESTERDAY_WORD)) {
            LocalTime time = LocalTime.parse(source.substring(source.length() - 5), TIME_FORMATTER);
            return LocalDate.now().minusDays(1).atTime(time);
        }
        if (source.matches(".*\\d{4}.*")) {
            try {
                return LocalDateTime.parse(source, DATE_WITH_TIME);
            } catch (DateTimeParseException ignored) {
                return OffsetDateTime.parse(parse).toLocalDateTime();
            }
        }
        LocalDateTime parsed = LocalDateTime.parse(source, DATE_WITHOUT_YEAR);
        return parsed.withYear(LocalDate.now().getYear());
    }
}

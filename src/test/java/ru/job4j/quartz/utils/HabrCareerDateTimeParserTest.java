package ru.job4j.quartz.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HabrCareerDateTimeParserTest {

    private static final String TODAY = "\u0441\u0435\u0433\u043e\u0434\u043d\u044f";
    private static final String YESTERDAY = "\u0432\u0447\u0435\u0440\u0430";
    private static final String DECEMBER = "\u0434\u0435\u043a\u0430\u0431\u0440\u044f";

    private final DateTimeParser parser = new HabrCareerDateTimeParser();

    @Test
    void whenIsoOffsetThenParsed() {
        LocalDateTime expected = LocalDateTime.of(2021, 12, 20, 18, 15, 10);
        LocalDateTime actual = parser.parse("2021-12-20T18:15:10+03:00");
        assertEquals(expected, actual);
    }

    @Test
    void whenTodayThenParsed() {
        LocalDateTime expected = LocalDate.now().atTime(12, 45);
        LocalDateTime actual = parser.parse(TODAY + ", 12:45");
        assertEquals(expected, actual);
    }

    @Test
    void whenYesterdayThenParsed() {
        LocalDateTime expected = LocalDate.now().minusDays(1).atTime(9, 5);
        LocalDateTime actual = parser.parse(YESTERDAY + ", 09:05");
        assertEquals(expected, actual);
    }

    @Test
    void whenDateWithYearThenParsed() {
        LocalDateTime expected = LocalDateTime.of(2021, 12, 17, 13, 11);
        LocalDateTime actual = parser.parse("17 " + DECEMBER + " 2021, 13:11");
        assertEquals(expected, actual);
    }
}

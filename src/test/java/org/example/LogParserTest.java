package org.example;

import org.example.exception.LogParsingException;
import org.example.logparser.LogParser;
import org.example.model.LogEntry;
import org.example.model.LogLevel;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    void parseAllLines_skipsInvalidLines_andParsesValidOnes() throws IOException {
        String input = """
                01-01-2024 10:00:00 INFO Main.java:42 App started
                garbage line that should be skipped
                01-01-2024 10:01:00 ERROR Main.java:99 Something went wrong
                """;

        LogParser parser = new LogParser();
        BufferedReader br = new BufferedReader(new StringReader(input));
        List<LogEntry> logs = parser.readLogFile(br);

        assertEquals(2, logs.size());
        assertEquals(LogLevel.INFO, logs.get(0).logLevel());
        assertEquals("App started", logs.get(0).message());
        assertEquals(LogLevel.ERROR, logs.get(1).logLevel());
        assertEquals("Something went wrong", logs.get(1).message());
    }

    @Test
    void parseInvalidLine() {
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12- 11:14:36 ERROR whateverfile.java:16 Failed to process payment")); // Poorly formatted date
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12-2025 11:14:0000 ERROR whateverfile.java:16 Failed to process payment")); // Poorly formatted time
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12-2025 11:14:36 AZER whateverfile.java:16 Failed to process payment")); // Undefined log level
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12-2025 11:14:36 ERROR :16 Failed to process payment")); // No file name
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java: Failed to process payment")); // No line number
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:16")); // No message
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:az Failed to process payment")); // Line num: NaN
        assertThrows(LogParsingException.class,
                () -> parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:16.5 Failed to process payment")); // Line num: double
    }

    @Test
    void convertStringToLocalDateTime() {
        LocalDateTime now = LocalDateTime.now(); // Get current date-time
        String nowString = now.format(parser.getFormatter()); // Format it and return a String
        now = LocalDateTime.parse(nowString, parser.getFormatter()); // Use the well formatted String to create a date-time

        assertEquals(now, parser.convertStringToLocalDateTime(nowString));
        assertThrows(DateTimeParseException.class, () -> parser.convertStringToLocalDateTime("wrong"));
    }
}

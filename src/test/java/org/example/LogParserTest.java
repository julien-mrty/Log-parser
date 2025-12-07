package org.example;

import org.example.logparser.LogParser;
import org.example.model.LogEntry;
import org.example.model.LogLevel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    void readFile() {
        String fileName = "log_example.log";
        String output = "";
        //LogParser.createJsonFromLogFile(fileName, output);
    }

    @Test
    void readWrongFile() {
        String fileName = "wrong.log";
        String output = "";
        //LogParser.createJsonFromLogFile(fileName, output);
    }

    @Test
    void parseLine() {
        assertEquals(Optional.empty(),
                parser.parseLine("02-12- 11:14:36 ERROR whateverfile.java:16 Failed to process payment")); // Poorly formatted date
        assertEquals(Optional.empty(),
                parser.parseLine("02-12-2025 11:14:0000 ERROR whateverfile.java:16 Failed to process payment")); // Poorly formatted time
        assertEquals(Optional.empty(),
                parser.parseLine("02-12-2025 11:14:36 AZER whateverfile.java:16 Failed to process payment")); // Undefined log level
        assertEquals(Optional.empty(),
                parser.parseLine("02-12-2025 11:14:36 ERROR :16 Failed to process payment")); // No file name
        assertEquals(Optional.empty(),
                parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java: Failed to process payment")); // No line number
        assertEquals(Optional.empty(),
                parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:16")); // No message
        assertEquals(Optional.empty(),
                parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:az Failed to process payment")); // Line num: NaN
        assertEquals(Optional.empty(),
                parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:16.5 Failed to process payment")); // Line num: NaN
    }

    @Test
    void parsePoorlyFormateLine() {
        Optional<LogEntry> entry = parser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:16 Failed to process payment");
        assertEquals(LocalDateTime.parse("02-12-2025 11:14:36", parser.getFormatter()), entry.get().dateTime());
        assertEquals(LogLevel.ERROR, entry.get().logLevel());
        assertEquals("whateverfile.java", entry.get().fileName());
        assertEquals(16, entry.get().line());
        assertEquals("Failed to process payment", entry.get().message());
    }

    @Test
    void convertStringToLogLevel() {
        assertEquals(LogLevel.FATAL, LogParser.convertStringToLogLevel("FATAL").get());
        assertEquals(LogLevel.ERROR, LogParser.convertStringToLogLevel("ERROR").get());
        assertEquals(LogLevel.WARNING, LogParser.convertStringToLogLevel("WARNING").get());
        assertEquals(LogLevel.INFO, LogParser.convertStringToLogLevel("INFO").get());
        assertEquals(LogLevel.DEBUG, LogParser.convertStringToLogLevel("DEBUG").get());
        assertEquals(LogLevel.TRACE, LogParser.convertStringToLogLevel("TRACE").get());
        assertEquals(Optional.empty(), LogParser.convertStringToLogLevel("wrong"));
    }

    @Test
    void convertStringToLocalDateTime() {
        LocalDateTime now = LocalDateTime.now(); // Get current date-time
        String nowString = now.format(parser.getFormatter()); // Format it and return a String
        now = LocalDateTime.parse(nowString, parser.getFormatter()); // Use the well formatted String to create a date-time

        assertEquals(now, parser.convertStringToLocalDateTime(nowString).get());
        assertEquals(Optional.empty(), parser.convertStringToLocalDateTime("wrong"));
    }
}

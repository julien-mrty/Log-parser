package org.example;

import org.example.model.LogEntry;
import org.example.model.LogLevel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.*;

public class LogParser {

    private final DateTimeFormatter formatter;
    private final Pattern logPattern;

    LogParser() {
        this.formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.logPattern = Pattern.compile(
                "^(?<date>\\d{2}-\\d{2}-\\d{4}) " +
                        "(?<time>\\d{2}:\\d{2}:\\d{2}) " +
                        "(?<level>\\w+) " +
                        "(?<file>[^:]+):(?<line>\\d+) " +
                        "(?<message>.*)$"
        );
    }

    LogParser(DateTimeFormatter formatter, Pattern logPattern) {
        this.formatter = formatter;
        this.logPattern = logPattern;
    }

    public void createJsonFromLogFile(String logFileName, String JsonFileName) {
        List<LogEntry> logs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(logFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                Optional<LogEntry> log = parseLine(line);
                log.ifPresent(logs::add);
            }
        }
        catch (IOException e) {
            System.err.println("Error reading file: " + logFileName + ", " + e);
        }
    }

    public Optional<LogEntry> parseLine(String line) {
        Matcher matcher = logPattern.matcher(line);

        if (!matcher.matches()) {
            System.out.println("No match found for: " + line);
            return Optional.empty();
        }
        System.out.println("matcher.group(\"date\"): " + matcher.group("date"));

        LocalDateTime dateTime = convertStringToLocalDateTime(matcher.group("date") + " " + matcher.group("time"));

        Optional<LogLevel> logLevel = convertStringToLogLevel(matcher.group("level"));
        if (logLevel.isEmpty()) {
            System.out.println("Log level, incorrect format: " + line);
            return Optional.empty();
        }

        int lineNum;
        try {
            lineNum = Integer.parseInt(matcher.group("line"));
        }
        catch (NumberFormatException e) {
            System.out.println("Line number, incorrect format: " + line);
            return Optional.empty();
        }

        return Optional.of(new LogEntry(
                dateTime,
                logLevel.get(),
                matcher.group("file"),
                lineNum,
                matcher.group("message")
        ));
    }

    public static Optional<LogLevel> convertStringToLogLevel(String in) {
        return switch (in) {
            case "FATAL" -> Optional.of(LogLevel.FATAL);
            case "ERROR" -> Optional.of(LogLevel.ERROR);
            case "WARNING" -> Optional.of(LogLevel.WARNING);
            case "INFO" -> Optional.of(LogLevel.INFO);
            case "DEBUG" -> Optional.of(LogLevel.DEBUG);
            case "TRACE" -> Optional.of(LogLevel.TRACE);
            default -> Optional.empty();
        };
    }

    public LocalDateTime convertStringToLocalDateTime(String in) {
        return LocalDateTime.parse(in, formatter);
    }
}

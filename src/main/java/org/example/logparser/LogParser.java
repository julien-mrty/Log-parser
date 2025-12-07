package org.example.logparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.factory.ObjectMapperFactory;
import org.example.model.LogEntry;
import org.example.model.LogLevel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private final DateTimeFormatter formatter;
    private final Pattern logPattern;

    public LogParser() {
        this.formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.logPattern = Pattern.compile(
                "^(?<date>\\d{2}-\\d{2}-\\d{4}) " +
                        "(?<time>\\d{2}:\\d{2}:\\d{2}) " +
                        "(?<level>\\w+) " +
                        "(?<file>[^:]+):(?<line>\\d+) " +
                        "(?<message>.*)$"
        );
    }

    /*
    LogParser(DateTimeFormatter formatter, Pattern logPattern) {
        this.formatter = formatter;
        this.logPattern = logPattern;
    }
    */

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void createJsonFromLogFile(String logFileName, Path outputFilePath) {
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

        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        String jsonString;

        try {
            jsonString = mapper.writeValueAsString(logs);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.write(outputFilePath, jsonString.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<LogEntry> parseLine(String line) {
        Matcher matcher = logPattern.matcher(line);
        if (!matcher.matches()) {
            System.out.println("No match found for: " + line);
            return Optional.empty();
        }

        // If poorly formatted would be rejected by the pattern matcher
        Optional<LocalDateTime> dateTime = convertStringToLocalDateTime(matcher.group("date") + " " + matcher.group("time"));

        Optional<LogLevel> logLevel = convertStringToLogLevel(matcher.group("level"));
        if (logLevel.isEmpty()) {
            System.out.println("Log level, incorrect format: " + line);
            return Optional.empty();
        }

        // If it isn't an int it would be rejected by the pattern matcher
        int lineNum = Integer.parseInt(matcher.group("line"));

        return Optional.of(new LogEntry(
                dateTime.get(),
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

    public Optional<LocalDateTime> convertStringToLocalDateTime(String in) {
        Optional<LocalDateTime> dateTime;
        try {
            dateTime = Optional.of(LocalDateTime.parse(in, formatter));
        }
        catch (DateTimeParseException e) {
            return Optional.empty();
        }

        return dateTime;
    }
}

package org.example.logparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.LogParsingException;
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

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public void createJsonFromLogFile(Path logFilePath, Path outputFilePath) {
        List<LogEntry> logs = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(logFilePath.toFile()));
            logs = readLogFile(br);
        } catch (IOException e) {
            System.out.println("Error reading file: " + logFilePath + ", " + e.getMessage());
        }

        try {
            ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
            String jsonString = mapper.writeValueAsString(logs);
            Files.write(outputFilePath, jsonString.getBytes());
        } catch (JsonProcessingException e) {
            System.out.println("Error while mapping the logs to json formatted string: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error while writing the file: " + outputFilePath + ", " + e.getMessage());
        }
    }

    public List<LogEntry> readLogFile(BufferedReader br) throws IOException {
        List<LogEntry> logs = new ArrayList<>();
        String line;

        while ((line = br.readLine()) != null) {
            try {
                logs.add(parseLine(line));
            } catch (LogParsingException e) {
                System.out.println("Skipping invalid line: " + line);
            }
        }

        return logs;
    }

    public LogEntry parseLine(String line) {
        Matcher matcher = logPattern.matcher(line);
        if (!matcher.matches()) {
            throw new LogParsingException("Line does not match log pattern: " + line);
        }

        LocalDateTime dateTime;
        try {
            dateTime = convertStringToLocalDateTime(matcher.group("date") + " " + matcher.group("time"));
        } catch (DateTimeParseException e) {
            throw new LogParsingException("Invalid date/time: " + line + ", " + e.getMessage());
        }

        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(matcher.group("level"));
        } catch (IllegalArgumentException e) {
            throw new LogParsingException("Unknown log level: " + line + ", " + e.getMessage());
        }

        // If it isn't an int it would be rejected by the pattern matcher
        int lineNum = Integer.parseInt(matcher.group("line"));

        return new LogEntry(
                dateTime,
                logLevel,
                matcher.group("file"),
                lineNum,
                matcher.group("message")
        );
    }

    public LocalDateTime convertStringToLocalDateTime(String in) {
        return LocalDateTime.parse(in, formatter);
    }
}

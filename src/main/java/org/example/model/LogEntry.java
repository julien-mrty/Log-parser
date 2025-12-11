package org.example.model;

import java.time.LocalDateTime;

public record LogEntry(
        LocalDateTime dateTime,
        LogLevel logLevel,
        String fileName,
        int line,
        String message) {
}

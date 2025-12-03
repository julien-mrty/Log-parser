package org.example;

import org.example.model.LogLevel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions.*;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogParserTest {

    private LogParser parser = new LogParser();

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
        //LogParser.parseLine("02-12-2025 11:14:36 ERROR whateverfile.java:16 Failed to process payment");
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
}

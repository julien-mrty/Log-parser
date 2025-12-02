package org.example;

import org.junit.jupiter.api.*;

public class LogParserTest {

    @BeforeAll
    static void initialisation() {

    }

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
}

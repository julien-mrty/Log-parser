package org.example;

import org.example.logparser.LogParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        /*
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = formatter.format(date);
        System.out.println(formattedDate);
        */

        String fileName = "log_example.log";
        Path outputFilePath = Paths.get("output.json");
        LogParser logParser = new LogParser();
        logParser.createJsonFromLogFile(fileName, outputFilePath);
    }
}
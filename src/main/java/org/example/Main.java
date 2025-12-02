package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        /*
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDate = formatter.format(date);
        System.out.println(formattedDate);
        */

        String fileName = "log_example.log";
        String output = "";
        LogParser.createJsonFromLogFile(fileName, output);
    }
}
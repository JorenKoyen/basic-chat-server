package be.kdg.chat.util;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Logger {
    private final String name;
    private final int classColor;


    public static Logger getLogger(String origin) {
        return new Logger(origin);
    }

    private Logger(String name) {
        this.name = name;
        // exclude first index to not get reset code
        this.classColor = 1 + ThreadLocalRandom.current().nextInt(AnsiCode.values().length - 1);
    }

    public void info(String message) {
        System.out.println(formatMessage(message));
    }
    public void error(String message) {
        System.err.println(formatMessage(message));
    }

    private String formatMessage(String message) {
        return String.format("%-30s %s\t\t%s",
                formatName(),
                TextColor.printAsColor(AnsiCode.YELLOW, new Date().toString()),
                message);
    }

    private String formatName() {
        return String.format("[%s]", TextColor.printAsColor(AnsiCode.values()[this.classColor], this.name));
    }



}

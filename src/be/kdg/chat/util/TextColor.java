package be.kdg.chat.util;

public class TextColor {
    public static String printAsColor(AnsiCode code, String text) {
        return printAsColor(code, text, true);
    }
    public static String printAsColor(AnsiCode code, String text, boolean reset) {
        return String.format("%s%s%s", code.getCode(), text, reset ? AnsiCode.RESET.getCode() : "");
    }
}

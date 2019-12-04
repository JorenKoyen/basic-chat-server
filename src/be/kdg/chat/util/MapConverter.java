package be.kdg.chat.util;

import java.util.Map;

public class MapConverter {
    public static String convertToString(Map<String, String> map) {
        if (map.size() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder("{");
        for (String key : map.keySet()) {
            builder.append(key + "=" + map.get(key) + ", ");
        }

        // remove last comma and append bracket
        builder
            .delete(builder.length() - 2, builder.length())
            .append("}");

        return builder.toString();
    }
}

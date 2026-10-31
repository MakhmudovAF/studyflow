package studyflow.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class FormDataParser {
    private FormDataParser() {
    }

    public static Map<String, String> parse(String body) {
        Map<String, String> values = new HashMap<>();

        if (body == null || body.isBlank()) {
            return values;
        }

        for (String parameter : body.split("&")) {
            String[] parts = parameter.split("=", 2);

            String key = decode(parts[0]);
            String value = parts.length == 2 ? decode(parts[1]) : "";

            values.put(key, value);
        }

        return values;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
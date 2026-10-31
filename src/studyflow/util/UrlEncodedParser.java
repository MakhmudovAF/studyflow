package studyflow.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class UrlEncodedParser {
    private UrlEncodedParser() {
    }

    public static Map<String, String> parse(String value) {
        Map<String, String> parameters = new HashMap<>();

        if (value == null || value.isBlank()) {
            return parameters;
        }

        for (String parameter : value.split("&")) {
            String[] parts = parameter.split("=", 2);

            String key = decode(parts[0]);
            String parameterValue = parts.length == 2 ? decode(parts[1]) : "";

            parameters.put(key, parameterValue);
        }

        return parameters;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
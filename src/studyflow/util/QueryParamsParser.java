package studyflow.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class QueryParamsParser {
    private QueryParamsParser() {
    }

    public static Map<String, String> parse(String query) {
        Map<String, String> parameters = new HashMap<>();

        if (query == null || query.isBlank()) {
            return parameters;
        }

        for (String parameter : query.split("&")) {
            String[] parts = parameter.split("=", 2);

            String key = decode(parts[0]);
            String value = parts.length == 2 ? decode(parts[1]) : "";

            parameters.put(key, value);
        }

        return parameters;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
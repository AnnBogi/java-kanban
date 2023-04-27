package canban.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParamExtractorUtils {
    public static Map<String, String> queryToMap(String query) {
        if (query == null) {
            return Map.of();
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static Optional<Integer> getQueryParamInteger(Map<String, String> params, String key) {
        final String idRaw = params.get(key);
        if (idRaw == null || idRaw.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(idRaw));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
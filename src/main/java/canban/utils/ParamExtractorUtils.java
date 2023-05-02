package canban.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ParamExtractorUtils {

    private ParamExtractorUtils() {}

    public static Map<String, String> queryToMap(String query) {
        if (query == null) {
            return Map.of();
        }
        var result = new HashMap<String, String>();
        for (var param : query.split("&")) {
            var entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static Optional<Integer> getQueryParamInteger(Map<String, String> params, String key) {
        var idRaw = params.get(key);
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
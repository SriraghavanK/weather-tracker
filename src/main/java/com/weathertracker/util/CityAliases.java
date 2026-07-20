package com.weathertracker.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class CityAliases {

    private static final Map<String, List<String>> ALIASES = Map.of(
            "bangalore", List.of("Bengaluru"),
            "bombay", List.of("Mumbai"),
            "madras", List.of("Chennai"),
            "calcutta", List.of("Kolkata"),
            "poona", List.of("Pune"));

    private CityAliases() {
    }

    public static List<String> searchTermsFor(String cityName) {
        Set<String> terms = new LinkedHashSet<>();
        terms.add(cityName.trim());

        List<String> aliases = ALIASES.get(cityName.trim().toLowerCase(Locale.ROOT));
        if (aliases != null) {
            terms.addAll(aliases);
        }

        return new ArrayList<>(terms);
    }
}

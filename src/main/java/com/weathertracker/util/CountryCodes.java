package com.weathertracker.util;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class CountryCodes {

    private static final Map<String, String> COUNTRY_TO_CODE = Map.ofEntries(
            Map.entry("india", "IN"),
            Map.entry("in", "IN"),
            Map.entry("pakistan", "PK"),
            Map.entry("pk", "PK"),
            Map.entry("united kingdom", "GB"),
            Map.entry("uk", "GB"),
            Map.entry("england", "GB"),
            Map.entry("gb", "GB"),
            Map.entry("united states", "US"),
            Map.entry("usa", "US"),
            Map.entry("us", "US"),
            Map.entry("america", "US"),
            Map.entry("canada", "CA"),
            Map.entry("ca", "CA"),
            Map.entry("australia", "AU"),
            Map.entry("au", "AU"),
            Map.entry("germany", "DE"),
            Map.entry("de", "DE"),
            Map.entry("france", "FR"),
            Map.entry("fr", "FR"),
            Map.entry("japan", "JP"),
            Map.entry("jp", "JP"),
            Map.entry("china", "CN"),
            Map.entry("cn", "CN"),
            Map.entry("brazil", "BR"),
            Map.entry("br", "BR"),
            Map.entry("mexico", "MX"),
            Map.entry("mx", "MX"),
            Map.entry("spain", "ES"),
            Map.entry("es", "ES"),
            Map.entry("italy", "IT"),
            Map.entry("it", "IT"),
            Map.entry("uae", "AE"),
            Map.entry("united arab emirates", "AE"),
            Map.entry("ae", "AE"),
            Map.entry("singapore", "SG"),
            Map.entry("sg", "SG"),
            Map.entry("sri lanka", "LK"),
            Map.entry("lk", "LK"),
            Map.entry("nepal", "NP"),
            Map.entry("np", "NP"),
            Map.entry("bangladesh", "BD"),
            Map.entry("bd", "BD"));

    private CountryCodes() {
    }

    public static Optional<String> resolve(String countryText) {
        if (countryText == null || countryText.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(COUNTRY_TO_CODE.get(countryText.trim().toLowerCase(Locale.ROOT)));
    }
}

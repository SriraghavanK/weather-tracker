package com.weathertracker.util;

import com.weathertracker.model.LocationQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LocationQueryParser {

    private LocationQueryParser() {
    }

    public static LocationQuery parse(String input) {
        String trimmed = input.trim();

        if (trimmed.contains(",")) {
            String[] parts = trimmed.split(",", 2);
            String city = parts[0].trim();
            String countryHint = parts[1].trim();
            Optional<String> countryCode = CountryCodes.resolve(countryHint);
            return countryCode
                    .map(code -> LocationQuery.withCountry(city, countryHint, code))
                    .orElse(LocationQuery.ofCity(trimmed));
        }

        String[] words = trimmed.split("\\s+");
        if (words.length >= 2) {
            for (int countryWords = Math.min(3, words.length - 1); countryWords >= 1; countryWords--) {
                int cityWordCount = words.length - countryWords;
                String countryHint = joinWords(words, cityWordCount, words.length);
                Optional<String> countryCode = CountryCodes.resolve(countryHint);

                if (countryCode.isPresent()) {
                    String city = joinWords(words, 0, cityWordCount);
                    return LocationQuery.withCountry(city, countryHint, countryCode.get());
                }
            }
        }

        return LocationQuery.ofCity(trimmed);
    }

    private static String joinWords(String[] words, int startInclusive, int endExclusive) {
        List<String> slice = new ArrayList<>();
        for (int i = startInclusive; i < endExclusive; i++) {
            slice.add(words[i]);
        }
        return String.join(" ", slice);
    }
}

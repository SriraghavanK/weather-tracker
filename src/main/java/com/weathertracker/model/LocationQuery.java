package com.weathertracker.model;

import java.util.Optional;

public record LocationQuery(String cityName, Optional<String> countryHint, Optional<String> countryCode) {

    public static LocationQuery ofCity(String cityName) {
        return new LocationQuery(cityName, Optional.empty(), Optional.empty());
    }

    public static LocationQuery withCountry(String cityName, String countryHint, String countryCode) {
        return new LocationQuery(cityName, Optional.of(countryHint), Optional.of(countryCode));
    }
}

package com.weathertracker.model;

import java.util.List;

public record WeatherLookupResult(WeatherData weather, List<String> alerts) {
}

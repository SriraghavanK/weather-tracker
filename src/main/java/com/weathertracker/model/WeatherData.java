package com.weathertracker.model;

public record WeatherData(
        String cityName,
        double latitude,
        double longitude,
        double temperature,
        int weatherCode,
        double windSpeed,
        String condition) {
}

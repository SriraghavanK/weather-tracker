package com.weathertracker.service;

import com.weathertracker.client.NominatimClient;
import com.weathertracker.client.OpenMeteoClient;
import com.weathertracker.exception.WeatherException;
import com.weathertracker.model.Coordinates;
import com.weathertracker.model.WeatherData;
import com.weathertracker.model.WeatherLookupResult;
import com.weathertracker.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WeatherService {

    private static final Pattern COORDINATE_PATTERN =
            Pattern.compile("^\\s*(-?\\d+(?:\\.\\d+)?)\\s*[,\\s]\\s*(-?\\d+(?:\\.\\d+)?)\\s*$");

    private final OpenMeteoClient openMeteoClient;
    private final NominatimClient nominatimClient;
    private final HistoryRepository historyRepository;
    private final AlertService alertService;

    public WeatherService(
            OpenMeteoClient openMeteoClient,
            NominatimClient nominatimClient,
            HistoryRepository historyRepository,
            AlertService alertService) {
        this.openMeteoClient = openMeteoClient;
        this.nominatimClient = nominatimClient;
        this.historyRepository = historyRepository;
        this.alertService = alertService;
    }

    public WeatherLookupResult lookup(String input) {
        if (input == null || input.isBlank()) {
            throw new WeatherException("Please enter a city name or latitude/longitude coordinates.");
        }

        Coordinates coordinates = resolveCoordinates(input.trim());
        WeatherData weather = openMeteoClient.fetchWeather(coordinates);

        List<String> alerts = alertService.evaluate(weather);
        historyRepository.save(weather, !alerts.isEmpty());

        return new WeatherLookupResult(weather, alerts);
    }

    private Coordinates resolveCoordinates(String input) {
        Matcher matcher = COORDINATE_PATTERN.matcher(input);
        if (matcher.matches()) {
            double latitude = Double.parseDouble(matcher.group(1));
            double longitude = Double.parseDouble(matcher.group(2));
            validateCoordinates(latitude, longitude);

            String displayName = nominatimClient.reverseGeocode(latitude, longitude)
                    .orElseGet(() -> String.format("%.4f, %.4f", latitude, longitude));

            return new Coordinates(latitude, longitude, displayName);
        }

        return openMeteoClient.geocodeCity(input);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new WeatherException("Latitude must be between -90 and 90.");
        }
        if (longitude < -180 || longitude > 180) {
            throw new WeatherException("Longitude must be between -180 and 180.");
        }
    }
}

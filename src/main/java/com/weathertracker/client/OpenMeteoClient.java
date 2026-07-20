package com.weathertracker.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weathertracker.exception.WeatherException;
import com.weathertracker.model.Coordinates;
import com.weathertracker.model.LocationQuery;
import com.weathertracker.model.WeatherData;
import com.weathertracker.util.CityAliases;
import com.weathertracker.util.LocationQueryParser;
import com.weathertracker.util.WeatherCodeMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class OpenMeteoClient {

    private static final int GEOCODE_RESULT_LIMIT = 10;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String weatherUrl;
    private final String geocodingUrl;
    private final long readTimeoutMs;

    public OpenMeteoClient(
            HttpClient httpClient,
            ObjectMapper objectMapper,
            @Value("${open-meteo.weather-url}") String weatherUrl,
            @Value("${open-meteo.geocoding-url}") String geocodingUrl,
            @Value("${open-meteo.read-timeout-ms}") long readTimeoutMs) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.weatherUrl = weatherUrl;
        this.geocodingUrl = geocodingUrl;
        this.readTimeoutMs = readTimeoutMs;
    }

    public Coordinates geocodeCity(String userInput) {
        LocationQuery query = LocationQueryParser.parse(userInput);
        List<String> searchTerms = CityAliases.searchTermsFor(query.cityName());

        for (String searchTerm : searchTerms) {
            if (query.countryCode().isPresent()) {
                Coordinates match = searchWithCountryFilter(searchTerm, query);
                if (match != null) {
                    return match;
                }
            }

            Coordinates match = searchBestMatch(searchTerm, query);
            if (match != null) {
                return match;
            }
        }

        throw new WeatherException("City not found: " + userInput);
    }

    public WeatherData fetchWeather(Coordinates coordinates) {
        String url = weatherUrl
                + "?latitude=" + coordinates.latitude()
                + "&longitude=" + coordinates.longitude()
                + "&current=temperature_2m,weather_code,wind_speed_10m";

        JsonNode root = getJson(url);
        JsonNode current = root.path("current");

        if (current.isMissingNode()) {
            throw new WeatherException("Weather data unavailable for the requested location.");
        }

        double temperature = current.path("temperature_2m").asDouble();
        int weatherCode = current.path("weather_code").asInt();
        double windSpeed = current.path("wind_speed_10m").asDouble();
        String condition = WeatherCodeMapper.toCondition(weatherCode);

        return new WeatherData(
                coordinates.displayName(),
                coordinates.latitude(),
                coordinates.longitude(),
                temperature,
                weatherCode,
                windSpeed,
                condition);
    }

    private Coordinates searchWithCountryFilter(String searchTerm, LocationQuery query) {
        String countryCode = query.countryCode().orElseThrow();
        String url = buildGeocodingUrl(searchTerm, countryCode);
        JsonNode results = getGeocodingResults(url);

        if (results.isEmpty()) {
            return null;
        }

        return toCoordinates(pickBestResult(results, searchTerm, query));
    }

    private Coordinates searchBestMatch(String searchTerm, LocationQuery query) {
        String url = buildGeocodingUrl(searchTerm, null);
        JsonNode results = getGeocodingResults(url);

        if (results.isEmpty()) {
            return null;
        }

        JsonNode best = pickBestResult(results, searchTerm, query);
        if (best == null) {
            return null;
        }

        return toCoordinates(best);
    }

    private JsonNode getGeocodingResults(String url) {
        JsonNode root = getJson(url);
        JsonNode results = root.path("results");
        return results.isArray() ? results : root.path("missing");
    }

    private String buildGeocodingUrl(String searchTerm, String countryCode) {
        String encodedName = URLEncoder.encode(searchTerm.trim(), StandardCharsets.UTF_8);
        StringBuilder url = new StringBuilder(geocodingUrl)
                .append("?name=")
                .append(encodedName)
                .append("&count=")
                .append(GEOCODE_RESULT_LIMIT)
                .append("&language=en&format=json");

        if (countryCode != null && !countryCode.isBlank()) {
            url.append("&countryCode=").append(countryCode);
        }

        return url.toString();
    }

    private JsonNode pickBestResult(JsonNode results, String searchTerm, LocationQuery query) {
        String normalizedSearch = searchTerm.trim().toLowerCase(Locale.ROOT);

        return java.util.stream.StreamSupport.stream(results.spliterator(), false)
                .max(Comparator.comparingInt(result -> scoreResult(result, normalizedSearch, query)))
                .filter(result -> scoreResult(result, normalizedSearch, query) > 0)
                .orElse(null);
    }

    private int scoreResult(JsonNode result, String normalizedSearch, LocationQuery query) {
        String name = result.path("name").asText("").toLowerCase(Locale.ROOT);
        String country = result.path("country").asText("").toLowerCase(Locale.ROOT);
        String countryCode = result.path("country_code").asText("").toLowerCase(Locale.ROOT);
        String featureCode = result.path("feature_code").asText("");
        int population = result.path("population").asInt(0);

        int score = 0;

        if (name.equals(normalizedSearch)) {
            score += 5_000;
        } else if (name.contains(normalizedSearch) || normalizedSearch.contains(name)) {
            score += 1_000;
        }

        score += switch (featureCode) {
            case "PPLC", "PPLA" -> 2_000;
            case "PPL" -> 1_000;
            case "PPLX" -> -2_000;
            default -> 0;
        };

        if (population > 0) {
            score += (int) Math.min(3_000, Math.log10(population) * 500);
        }

        if (query.countryCode().isPresent()) {
            String expectedCode = query.countryCode().get().toLowerCase(Locale.ROOT);
            if (countryCode.equals(expectedCode)) {
                score += 10_000;
            } else {
                score -= 5_000;
            }
        }

        if (query.countryHint().isPresent()) {
            String expectedCountry = query.countryHint().get().toLowerCase(Locale.ROOT);
            if (country.equals(expectedCountry) || country.contains(expectedCountry)) {
                score += 8_000;
            }
        }

        return score;
    }

    private Coordinates toCoordinates(JsonNode result) {
        double latitude = result.path("latitude").asDouble();
        double longitude = result.path("longitude").asDouble();
        String name = result.path("name").asText();
        String country = result.path("country").asText("");
        String displayName = country.isBlank() ? name : name + ", " + country;
        return new Coordinates(latitude, longitude, displayName);
    }

    private JsonNode getJson(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(readTimeoutMs))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new WeatherException("Weather API returned status " + response.statusCode());
            }

            return objectMapper.readTree(response.body());
        } catch (WeatherException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new WeatherException("Network error while contacting weather service.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new WeatherException("Weather request was interrupted.", ex);
        } catch (Exception ex) {
            throw new WeatherException("Failed to read weather API response.", ex);
        }
    }
}

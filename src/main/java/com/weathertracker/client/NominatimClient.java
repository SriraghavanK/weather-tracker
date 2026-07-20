package com.weathertracker.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Component
public class NominatimClient {

    private static final Logger log = LoggerFactory.getLogger(NominatimClient.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String reverseUrl;
    private final String userAgent;
    private final long readTimeoutMs;

    public NominatimClient(
            HttpClient httpClient,
            ObjectMapper objectMapper,
            @Value("${nominatim.reverse-url}") String reverseUrl,
            @Value("${nominatim.user-agent}") String userAgent,
            @Value("${open-meteo.read-timeout-ms}") long readTimeoutMs) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.reverseUrl = reverseUrl;
        this.userAgent = userAgent;
        this.readTimeoutMs = readTimeoutMs;
    }

    public Optional<String> reverseGeocode(double latitude, double longitude) {
        String url = reverseUrl
                + "?lat=" + latitude
                + "&lon=" + longitude
                + "&format=json"
                + "&addressdetails=1"
                + "&accept-language=en";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(readTimeoutMs))
                .header("User-Agent", userAgent)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("Reverse geocoding failed with status {}", response.statusCode());
                return Optional.empty();
            }

            JsonNode root = objectMapper.readTree(response.body());
            return parseLocationName(root);
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.warn("Reverse geocoding unavailable: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<String> parseLocationName(JsonNode root) {
        JsonNode address = root.path("address");
        if (address.isMissingNode()) {
            return Optional.empty();
        }

        String city = firstNonBlank(
                address.path("city").asText(null),
                address.path("town").asText(null),
                address.path("village").asText(null),
                address.path("municipality").asText(null),
                address.path("county").asText(null),
                address.path("state_district").asText(null));

        String country = address.path("country").asText(null);

        if (city != null && country != null) {
            return Optional.of(city + ", " + country);
        }
        if (city != null) {
            return Optional.of(city);
        }
        if (country != null) {
            return Optional.of(country);
        }

        String displayName = root.path("display_name").asText(null);
        if (displayName != null && !displayName.isBlank()) {
            return Optional.of(simplifyDisplayName(displayName));
        }

        return Optional.empty();
    }

    private String simplifyDisplayName(String displayName) {
        String[] parts = displayName.split(",");
        if (parts.length >= 2) {
            return parts[0].trim() + ", " + parts[parts.length - 1].trim();
        }
        return displayName.trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}

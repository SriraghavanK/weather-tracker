package com.weathertracker.controller;

import com.weathertracker.exception.WeatherException;
import com.weathertracker.model.WeatherData;
import com.weathertracker.model.WeatherLookupResult;
import com.weathertracker.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> getWeather(@RequestParam String location) {
        try {
            WeatherLookupResult result = weatherService.lookup(location);
            WeatherData weather = result.weather();
            
            Map<String, Object> response = new HashMap<>();
            response.put("cityName", weather.cityName());
            response.put("latitude", weather.latitude());
            response.put("longitude", weather.longitude());
            response.put("temperature", weather.temperature());
            response.put("condition", weather.condition());
            response.put("windSpeed", weather.windSpeed());
            response.put("alerts", result.alerts());
            
            return ResponseEntity.ok(response);
        } catch (WeatherException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

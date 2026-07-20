package com.weathertracker.service;

import com.weathertracker.model.WeatherData;
import com.weathertracker.util.WeatherCodeMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {

    public List<String> evaluate(WeatherData weather) {
        List<String> alerts = new ArrayList<>();

        if (weather.temperature() > 30) {
            alerts.add("Alert: Extreme heat detected!");
        }

        if (WeatherCodeMapper.isRain(weather.weatherCode())) {
            alerts.add("Alert: Bring an umbrella!");
        }

        return alerts;
    }
}

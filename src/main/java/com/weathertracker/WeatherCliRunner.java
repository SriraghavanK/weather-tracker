package com.weathertracker;

import com.weathertracker.exception.WeatherException;
import com.weathertracker.model.WeatherData;
import com.weathertracker.service.WeatherService;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Scanner;

// @Component - Disabled since we now use REST API
// public class WeatherCliRunner implements CommandLineRunner {
public class WeatherCliRunner implements CommandLineRunner {

    private final WeatherService weatherService;

    public WeatherCliRunner(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Mini Weather Tracker & Alerts ===");
        System.out.println("Enter a city name or coordinates (lat, lon). Type 'quit' to exit.");
        System.out.println();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Location: ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }

                if (input.isEmpty()) {
                    System.out.println("Error: Input cannot be empty.");
                    continue;
                }

                try {
                    var result = weatherService.lookup(input);
                    printWeather(result.weather());
                    printAlerts(result.alerts());
                } catch (WeatherException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

                System.out.println();
            }
        }
    }

    private void printWeather(WeatherData weather) {
        System.out.println();
        System.out.println("Location:   " + weather.cityName());
        System.out.printf("Temperature: %.1f°C%n", weather.temperature());
        System.out.println("Condition:  " + weather.condition());
        System.out.printf("Wind speed:  %.1f km/h%n", weather.windSpeed());
    }

    private void printAlerts(List<String> alerts) {
        if (alerts.isEmpty()) {
            return;
        }

        System.out.println();
        for (String alert : alerts) {
            System.out.println(alert);
        }
    }
}

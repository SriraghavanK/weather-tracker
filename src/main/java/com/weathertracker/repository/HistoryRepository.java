package com.weathertracker.repository;

import com.weathertracker.model.WeatherData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class HistoryRepository {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JdbcTemplate jdbcTemplate;

    public HistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initTable();
    }

    private void initTable() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp TEXT NOT NULL,
                    city_name TEXT NOT NULL,
                    temperature REAL NOT NULL,
                    condition TEXT NOT NULL,
                    alert_triggered INTEGER NOT NULL
                )
                """);
    }

    public void save(WeatherData weather, boolean alertTriggered) {
        jdbcTemplate.update(
                """
                INSERT INTO history (timestamp, city_name, temperature, condition, alert_triggered)
                VALUES (?, ?, ?, ?, ?)
                """,
                LocalDateTime.now().format(TIMESTAMP_FORMAT),
                weather.cityName(),
                weather.temperature(),
                weather.condition(),
                alertTriggered ? 1 : 0);
    }
}

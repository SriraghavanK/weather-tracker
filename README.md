# Weather Tracker

## Project Overview

Weather Tracker is a lightweight full-stack application that allows users to look up weather information for a specific city or coordinate pair and receive simple weather-based alerts. The project combines a Java Spring Boot backend with a React frontend to provide a responsive user experience for checking current conditions and reviewing recent lookup activity.

The application was designed as a small-scale demo of integrating external weather APIs, managing application configuration, and persisting lookup history in a local SQLite database.

## Objectives

- Provide a simple weather lookup experience through a web interface
- Fetch real-time weather conditions from Open-Meteo
- Resolve location names with geocoding support from Open-Meteo and Nominatim
- Persist weather history locally for later reference
- Demonstrate a clean separation between frontend, backend, service logic, and data access layers

## Key Features

- Search weather by city name or coordinates
- Display temperature, weather condition, wind speed, latitude, and longitude
- Generate user-friendly alerts based on weather data
- Save weather lookup history to SQLite
- Serve the frontend through a Vite-powered React application
- Expose a REST API for weather requests

## Technical Stack

### Backend
- Java 17
- Spring Boot 3.2.5
- Maven
- SQLite JDBC
- Spring Web, Spring JDBC, Spring JSON

### Frontend
- React 19
- Vite
- Tailwind CSS
- Lucide icons

### External APIs
- Open-Meteo Forecast API
- Open-Meteo Geocoding API
- Nominatim Reverse Geocoding API

## Project Structure

- `src/main/java/com/weathertracker` - Main backend package
  - `controller` - REST endpoints
  - `service` - Business logic for weather lookup and alert handling
  - `repository` - Persistence logic for historical data
  - `model` - Data transfer objects and domain models
  - `client` - API clients for weather and geocoding services
  - `util` - Helper classes such as location parsing and weather code mapping
- `src/main/resources/application.properties` - Backend configuration values
- `frontend/src` - React application source files
- `frontend/package.json` - Frontend dependencies and scripts
- `weather_history.db` - Local SQLite database file

## Backend Architecture

The backend is organized around a service-oriented flow:

1. A controller receives weather requests from the frontend.
2. The weather service coordinates location lookup and weather retrieval.
3. External API clients fetch data from Open-Meteo and Nominatim.
4. Results are normalized into application models and returned to the client.
5. Lookup history is stored in SQLite through the repository layer.

This structure makes the application easier to extend with additional weather features or alternative data providers.

## Prerequisites

Before running the project, ensure you have:

- Java 17 or newer
- Maven installed and available in your PATH
- Node.js and npm installed

## Running the Backend

From the project root, run:

```bash
mvn spring-boot:run
```

The application will start on:

- http://localhost:8080

### Example API Call

```bash
http://localhost:8080/api/weather?location=London
```

This endpoint returns weather information as JSON, including location data, temperature, condition, wind speed, and alerts.

## Running the Frontend

In a separate terminal, run:

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at:

- http://localhost:5173

## Usage Workflow

1. Start the backend.
2. Start the frontend.
3. Open the frontend in your browser.
4. Enter a location such as a city name or coordinates.
5. Review the weather details and alerts returned by the app.

## Configuration Notes

The backend configuration is stored in `src/main/resources/application.properties` and includes:

- SQLite database URL
- Open-Meteo weather endpoint
- Open-Meteo geocoding endpoint
- Nominatim reverse geocoding endpoint
- Timeout settings for remote API calls

## Development Notes

- The project uses local file-based persistence via SQLite, so no separate database server is required.
- The frontend and backend are intentionally separated for easier development and testing.
- The app is suitable for local development, demos, and educational purposes.

## Future Improvements

Possible enhancements for the project include:

- More detailed alert rules and severity levels
- Hourly or daily weather forecasts
- User authentication and saved preferences
- Better UI design and chart-based weather visualization
- Deployment to a cloud platform

## Summary

Weather Tracker is a practical example of building a modern full-stack application that connects a React frontend to a Spring Boot backend, consumes third-party weather APIs, and stores data locally. It is a strong foundation for expanding into a more complete weather application.

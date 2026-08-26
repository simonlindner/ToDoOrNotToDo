package SimonLindner.ToDoOrNotToDo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import SimonLindner.ToDoOrNotToDo.dto.DailyWeatherDto;
import SimonLindner.ToDoOrNotToDo.dto.DayMatchDto;
import SimonLindner.ToDoOrNotToDo.service.WeatherService;
import SimonLindner.ToDoOrNotToDo.service.ActivityService;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final ActivityService activityService;

    public WeatherController(WeatherService weatherService, ActivityService activityService) {
        this.weatherService = weatherService;
        this.activityService = activityService;
    }

    @GetMapping
    public List<DailyWeatherDto> getWeatherForecast(
            @RequestParam(name = "lat", defaultValue = "50.9375") double latitude,
            @RequestParam(name = "lon", defaultValue = "6.9603") double longitude) {
        
        return weatherService.getWeatherForecast(latitude, longitude);
    }

    @GetMapping("/matches")
    public List<DayMatchDto> getMatches(
            @RequestParam(name = "lat", defaultValue = "50.9375") double latitude,
            @RequestParam(name = "lon", defaultValue = "6.9603") double longitude) {

        // 1. Wetterdaten abrufen (kommt aus dem Cache, falls vorhanden)
        List<DailyWeatherDto> forecast = weatherService.getWeatherForecast(latitude, longitude);

        // 2. Wetter mit den Aktivitäten aus PostgreSQL abgleichen
        return activityService.matchActivitiesWithWeather(forecast);
    }
}

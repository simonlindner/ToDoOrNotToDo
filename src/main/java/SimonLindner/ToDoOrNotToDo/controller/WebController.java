package SimonLindner.ToDoOrNotToDo.controller;

import SimonLindner.ToDoOrNotToDo.dto.DailyWeatherDto;
import SimonLindner.ToDoOrNotToDo.dto.DayMatchDto;
import SimonLindner.ToDoOrNotToDo.model.Activity;
import SimonLindner.ToDoOrNotToDo.service.ActivityService;
import SimonLindner.ToDoOrNotToDo.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebController {

    private final WeatherService weatherService;
    private final ActivityService activityService;

    public WebController(WeatherService weatherService, ActivityService activityService) {
        this.weatherService = weatherService;
        this.activityService = activityService;
    }

    // 1. Hauptseite / Dashboard mit Wetter & Aktivitäten
    @GetMapping("/")
    public String index(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "lat", required = false) Double lat,
            @RequestParam(name = "lon", required = false) Double lon,
            Model model) {

        double selectedLat = 50.9375; // Standard: Köln
        double selectedLon = 6.9603;
        String locationName = "Köln";

        if (city != null && !city.isBlank()) {
            double[] coords = weatherService.getCoordinatesForCity(city);
            selectedLat = coords[0];
            selectedLon = coords[1];
            locationName = city;
        } else if (lat != null && lon != null) {
            selectedLat = lat;
            selectedLon = lon;
            locationName = String.format("Koord: %.2f, %.2f", lat, lon);
        }

        List<DailyWeatherDto> forecast = weatherService.getWeatherForecast(selectedLat, selectedLon);
        List<DayMatchDto> matches = activityService.matchActivitiesWithWeather(forecast);

        model.addAttribute("matches", matches);
        model.addAttribute("selectedLat", selectedLat);
        model.addAttribute("selectedLon", selectedLon);
        model.addAttribute("locationName", locationName);

        return "index";
    }

    // 2. Seite zur Verwaltung von Aktivitäten (Anzeigen & Erstellen)
    @GetMapping("/activities")
    public String showActivities(Model model) {
        model.addAttribute("activities", activityService.getAllActivities());
        model.addAttribute("newActivity", new Activity());
        return "activities";
    }

    // 3. Formular-Aktion zum Speichern einer neuen Aktivität
    @PostMapping("/activities")
    public String saveActivity(@ModelAttribute("newActivity") Activity activity) {
        activityService.saveActivity(activity);
        return "redirect:/activities";
    }
}
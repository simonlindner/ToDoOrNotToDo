package SimonLindner.ToDoOrNotToDo.controller;

import SimonLindner.ToDoOrNotToDo.dto.ActivityMatchDto;
import SimonLindner.ToDoOrNotToDo.dto.DailyWeatherDto;
import SimonLindner.ToDoOrNotToDo.model.Activity;
import SimonLindner.ToDoOrNotToDo.service.ActivityService;
import SimonLindner.ToDoOrNotToDo.service.WeatherService;
import jakarta.servlet.http.HttpSession;

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

    @GetMapping("/")
    public String index(
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "lat", required = false) Double lat,
            @RequestParam(name = "lon", required = false) Double lon,
            @RequestParam(name = "useNearestCity", required = false, defaultValue = "false") Boolean useNearestCity,
            HttpSession session,
            Model model) {

        // Fallback aus Session oder Standard Köln
        Double currentLat = (Double) session.getAttribute("lastLat");
        Double currentLon = (Double) session.getAttribute("lastLon");
        String currentName = (String) session.getAttribute("lastName");

        if (currentLat == null) {
            currentLat = 50.9375;
            currentLon = 6.9603;
            currentName = "Köln";
        }

        boolean invalidCity = false;

        if (city != null && !city.isBlank()) {
            double[] coords = weatherService.getCoordinatesForCity(city);
            if (coords != null) {
                currentLat = coords[0];
                currentLon = coords[1];
                currentName = city;
                session.setAttribute("lastLat", currentLat);
                session.setAttribute("lastLon", currentLon);
                session.setAttribute("lastName", currentName);
            } else {
                // Ort existiert nicht: Roter Rahmen, Standort der Session bleibt erhalten!
                invalidCity = true;
            }
        } else if (lat != null && lon != null) {
            currentLat = lat;
            currentLon = lon;
            
            if (Boolean.TRUE.equals(useNearestCity)) {
                String nearestName = weatherService.getCityForCoordinates(lat, lon);
                currentName = (nearestName != null) ? nearestName : String.format("Ort (%.2f, %.2f)", lat, lon);
            } else {
                currentName = String.format("%.2f, %.2f", lat, lon);
            }

            session.setAttribute("lastLat", currentLat);
            session.setAttribute("lastLon", currentLon);
            session.setAttribute("lastName", currentName);
        }

        List<DailyWeatherDto> forecast = weatherService.getWeatherForecast(currentLat, currentLon);
        List<ActivityMatchDto> activityMatches = activityService.getMatchCardData(forecast);

        model.addAttribute("activityMatches", activityMatches);
        model.addAttribute("selectedLat", currentLat);
        model.addAttribute("selectedLon", currentLon);
        model.addAttribute("locationName", currentName);
        model.addAttribute("cityInput", invalidCity ? city : currentName);
        model.addAttribute("invalidCity", invalidCity);

        return "index";
    }

    @PostMapping("/activities")
    public String saveActivity(@ModelAttribute Activity activity) {
        activityService.saveActivity(activity);
        return "redirect:/";
    }

    @PostMapping("/activities/{id}/delete")
public String deleteActivity(@PathVariable Long id) {
    activityService.deleteActivity(id);
    return "redirect:/";
}
}
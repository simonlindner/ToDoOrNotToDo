package SimonLindner.ToDoOrNotToDo.service;

import SimonLindner.ToDoOrNotToDo.dto.DailyWeatherDto;
import SimonLindner.ToDoOrNotToDo.dto.DayMatchDto;
import SimonLindner.ToDoOrNotToDo.model.Activity;
import SimonLindner.ToDoOrNotToDo.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Activity saveActivity(Activity activity) {
        if (activity.getMinTemp() != null && activity.getMaxTemp() != null) {
            if (activity.getMinTemp() > activity.getMaxTemp()) {
                throw new IllegalArgumentException("Min-Temperatur darf nicht größer als Max-Temperatur sein!");
            }
        }
        return activityRepository.save(activity);
    }

    public List<DayMatchDto> matchActivitiesWithWeather(List<DailyWeatherDto> forecast) {
        // 1. Alle Aktivitäten einmalig aus PostgreSQL laden
        List<Activity> allActivities = activityRepository.findAll();
        
        List<DayMatchDto> matches = new ArrayList<>();

        // 2. Jeden Tag der Vorhersage einzeln durchgehen
        for (DailyWeatherDto dayWeather : forecast) {
            
            // 3. Alle Aktivitäten filtern, die zu den Tageswerten passen
            List<Activity> suitableActivities = allActivities.stream()
                    .filter(activity -> isActivitySuitable(activity, dayWeather))
                    .toList();

            matches.add(new DayMatchDto(dayWeather.date(), dayWeather, suitableActivities));
        }

        return matches;
    }

    /**
     * Prüft, ob eine einzelne Aktivität zum Wetter eines Tages passt.
     */
    private boolean isActivitySuitable(Activity activity, DailyWeatherDto weather) {
        // Prüfung Indoor vs. Outdoor
        boolean isIndoor = Boolean.TRUE.equals(activity.getIsIndoor());
        
        // Wenn es eine Indoor-Aktivität ist, passt sie immer
        if (isIndoor) {
            return true;
        }

        // --- Ab hier gelten die Regeln für Outdoor-Aktivitäten ---

        // 1. Regen-Prüfung: Wenn Sonnenlicht/Trockenheit verlangt ist, darf die Regenwahrscheinlichkeit nicht zu hoch sein (z.B. max 30%)
        if (Boolean.TRUE.equals(activity.getRequiresSunlight()) && weather.rainProbability() > 30) {
            return false;
        }

        // 2. Mindesttemperatur-Prüfung
        if (activity.getMinTemp() != null && weather.maxTemp() < activity.getMinTemp()) {
            return false;
        }

        // 3. Maximaltemperatur-Prüfung
        if (activity.getMaxTemp() != null && weather.maxTemp() > activity.getMaxTemp()) {
            return false;
        }

        return true;
    }
}

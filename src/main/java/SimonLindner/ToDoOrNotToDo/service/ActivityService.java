package SimonLindner.ToDoOrNotToDo.service;

import SimonLindner.ToDoOrNotToDo.dto.ActivityMatchDto;
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
        boolean isIndoor = Boolean.TRUE.equals(activity.isIndoor());

        // Wenn es eine Indoor-Aktivität ist, passt sie immer
        if (isIndoor) {
            return true;
        }

        // --- Ab hier gelten die Regeln für Outdoor-Aktivitäten ---

        // 1. Regen-Prüfung: Wenn Sonnenlicht/Trockenheit verlangt ist, darf die
        // Regenwahrscheinlichkeit nicht zu hoch sein (z.B. max 30%)
        if (Boolean.TRUE.equals(activity.isRequiresSunlight()) && weather.rainProbability() > 30) {
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

    public List<ActivityMatchDto> getMatchCardData(List<DailyWeatherDto> forecast) {
        List<Activity> activities = activityRepository.findAll();
        List<ActivityMatchDto> cardDataList = new ArrayList<>();

        for (Activity activity : activities) {
            List<ActivityMatchDto.DayResult> dayResults = new ArrayList<>();
            int possibleDaysCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (DailyWeatherDto weather : forecast) {
                boolean isPossible = true;

                // Indoor ist immer möglich
                if (!Boolean.TRUE.equals(activity.isIndoor())) {
                    if (Boolean.TRUE.equals(activity.isRequiresSunlight()) && weather.rainProbability() > 30) {
                        isPossible = false;
                        if (!failReasons.contains("Regenrisiko zu hoch (>30%)")) {
                            failReasons.add("Regenrisiko zu hoch (>30%)");
                        }
                    }
                    if (activity.getMinTemp() != null && weather.maxTemp() < activity.getMinTemp()) {
                        isPossible = false;
                        if (!failReasons.contains("Zu kalt (unter " + activity.getMinTemp() + "°C)")) {
                            failReasons.add("Zu kalt (unter " + activity.getMinTemp() + "°C)");
                        }
                    }
                    if (activity.getMaxTemp() != null && weather.maxTemp() > activity.getMaxTemp()) {
                        isPossible = false;
                        if (!failReasons.contains("Zu heiß (über " + activity.getMaxTemp() + "°C)")) {
                            failReasons.add("Zu heiß (über " + activity.getMaxTemp() + "°C)");
                        }
                    }
                }

                if (isPossible) {
                    possibleDaysCount++;
                }

                dayResults.add(new ActivityMatchDto.DayResult(
                        weather.date(),
                        weather.maxTemp(),
                        isPossible));
            }

            // Zusammenfassenden Grund für die Rückseite aufbauen
            String reasonSummary;
            if (Boolean.TRUE.equals(activity.isIndoor())) {
                reasonSummary = "Indoor-Aktivität: Immer verfügbar unabhängig vom Wetter.";
            } else if (possibleDaysCount == forecast.size()) {
                reasonSummary = "Perfekt! Die Wetterbedingungen sind diese Woche an allen 7 Tagen erfüllt.";
            } else if (failReasons.isEmpty()) {
                reasonSummary = "Gute Bedingungen an " + possibleDaysCount + " von 7 Tagen.";
            } else {
                reasonSummary = "An abgelehnten Tagen: " + String.join(", ", failReasons) + ".";
            }

            cardDataList.add(new ActivityMatchDto(
                    activity,
                    possibleDaysCount > 0,
                    reasonSummary,
                    dayResults));
        }

        return cardDataList;
    }

    public void deleteActivity(Long id) {
    activityRepository.deleteById(id);
}
}

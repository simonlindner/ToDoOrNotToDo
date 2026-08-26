package SimonLindner.ToDoOrNotToDo.dto;

import java.util.List;
import java.time.LocalDate;
import SimonLindner.ToDoOrNotToDo.model.Activity;

public record DayMatchDto(
    LocalDate date,
    DailyWeatherDto weather,
    List<Activity> matchingActivities
) {}

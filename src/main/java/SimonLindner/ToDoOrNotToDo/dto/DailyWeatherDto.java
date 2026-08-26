package SimonLindner.ToDoOrNotToDo.dto;

import java.time.LocalDate;

public record DailyWeatherDto(
    LocalDate date,
    Double maxTemp,
    Double minTemp,
    Integer rainProbability
) {}

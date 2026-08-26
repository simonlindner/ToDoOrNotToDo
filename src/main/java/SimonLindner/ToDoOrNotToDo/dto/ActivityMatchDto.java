package SimonLindner.ToDoOrNotToDo.dto;

import java.util.List;
import java.time.LocalDate;
import SimonLindner.ToDoOrNotToDo.model.Activity;


public record ActivityMatchDto(
    Activity activity,
    boolean hasPossibleDays,
    String reasonSummary,
    List<DayResult> dayResults
) {
    public record DayResult(
        LocalDate date,
        double maxTemp,
        boolean isPossible
    ) {}
}
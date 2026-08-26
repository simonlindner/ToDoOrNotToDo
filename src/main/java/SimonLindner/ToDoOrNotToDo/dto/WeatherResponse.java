package SimonLindner.ToDoOrNotToDo.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record WeatherResponse(DailyWeatherDto daily) {
    public record DailyWeatherDto(
        List<String> time,

        @JsonProperty("temperature_2m_max")
        List<Double> temperature2mmax,

        @JsonProperty("temperature_2m_min")
        List<Double> temperature2mmin,

        @JsonProperty("precipitation_probability_max")
        List<Integer> precipitationProbabilityMax
    ) {}

}


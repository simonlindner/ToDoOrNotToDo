package SimonLindner.ToDoOrNotToDo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;

import SimonLindner.ToDoOrNotToDo.dto.DailyWeatherDto;
import SimonLindner.ToDoOrNotToDo.dto.WeatherResponse;

@Service
public class WeatherService {

    private final RestClient restClient;

    public WeatherService() {
        // Erstellt einen Standard-RestClient
        this.restClient = RestClient.create();
    }

    @Cacheable(value = "weatherCache", key = "#latitude + '_' + #longitude")
    public List<DailyWeatherDto> getWeatherForecast(double latitude, double longitude) {

        System.out.println("--> Live-API Aufruf an Open-Meteo für: " + latitude + ", " + longitude);

        // 1. Aufruf der externen REST-API
        WeatherResponse response = restClient.get()
                .uri("https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=Europe/Berlin",
                        latitude, longitude)
                .retrieve()
                .body(WeatherResponse.class);

        // 2. Umwandeln der parallelen Listen aus der API in unsere DailyWeatherDto
        // Objekte
        List<DailyWeatherDto> forecastList = new ArrayList<>();

        if (response != null && response.daily() != null) {
            var daily = response.daily();
            for (int i = 0; i < daily.time().size(); i++) {
                DailyWeatherDto dto = new DailyWeatherDto(
                        LocalDate.parse(daily.time().get(i)),
                        daily.temperature2mmax().get(i),
                        daily.temperature2mmin().get(i),
                        daily.precipitationProbabilityMax().get(i));
                forecastList.add(dto);
            }
        }

        return forecastList;
    }

    public double[] getCoordinatesForCity(String city) {
        try {
            // OpenStreetMap Nominatim API Aufruf
            var response = restClient.get()
                    .uri("https://nominatim.openstreetmap.org/search?q={city}&format=json&limit=1", city)
                    .header("User-Agent", "ToDoOrNotToDo-App") // Nominatim verlangt einen User-Agent Header
                    .retrieve()
                    .body(Object[].class);

            if (response != null && response.length > 0) {
                @SuppressWarnings("unchecked")
                Map<String, Object> firstMatch = (Map<String, Object>) response[0];
                double lat = Double.parseDouble((String) firstMatch.get("lat"));
                double lon = Double.parseDouble((String) firstMatch.get("lon"));
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.err.println("Fehler bei der Geokodierung: " + e.getMessage());
        }
        // Fallback: Köln
        return null;
    }

    public String getCityForCoordinates(double lat, double lon) {
        try {
            var response = restClient.get()
                    .uri("https://nominatim.openstreetmap.org/reverse?lat={lat}&lon={lon}&format=json", lat, lon)
                    .header("User-Agent", "ToDoOrNotToDo-App")
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("address")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> address = (Map<String, Object>) response.get("address");
                
                if (address.containsKey("city")) return (String) address.get("city");
                if (address.containsKey("town")) return (String) address.get("town");
                if (address.containsKey("village")) return (String) address.get("village");
                if (address.containsKey("municipality")) return (String) address.get("municipality");
                if (address.containsKey("county")) return (String) address.get("county");
            }
        } catch (Exception e) {
            System.err.println("Fehler bei Reverse-Geokodierung: " + e.getMessage());
        }
        return null;
    }
}
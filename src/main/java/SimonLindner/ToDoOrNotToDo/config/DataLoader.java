package SimonLindner.ToDoOrNotToDo.config;

import SimonLindner.ToDoOrNotToDo.model.Activity;
import SimonLindner.ToDoOrNotToDo.repository.ActivityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ActivityRepository activityRepository;

    public DataLoader(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Nur ausführen, wenn die Datenbank noch leer ist
        if (activityRepository.count() == 0) {
            
            // 1. Outdoor-Aktivität für warmes & sonniges Wetter
            activityRepository.save(Activity.builder()
                    .name("Fahrrad fahren")
                    .minTemp(18)
                    .maxTemp(32)
                    .requiresSunlight(true)
                    .isIndoor(false)
                    .build());

            // 2. Outdoor-Aktivität für kühles/mildes Wetter
            activityRepository.save(Activity.builder()
                    .name("Joggen")
                    .minTemp(10)
                    .maxTemp(22)
                    .requiresSunlight(false)
                    .isIndoor(false)
                    .build());

            // 3. Reines Sommer-Highlight
            activityRepository.save(Activity.builder()
                    .name("Freibad / Schwimmen")
                    .minTemp(25)
                    .maxTemp(40)
                    .requiresSunlight(true)
                    .isIndoor(false)
                    .build());

            // 4. Indoor-Aktivität (Wetterunabhängig)
            activityRepository.save(Activity.builder()
                    .name("Museumsbesuch / Kino")
                    .minTemp(null)
                    .maxTemp(null)
                    .requiresSunlight(false)
                    .isIndoor(true)
                    .build());

            System.out.println("--> DataLoader: 4 Test-Aktivitäten erfolgreich in PostgreSQL angelegt!");
        }
    }
}
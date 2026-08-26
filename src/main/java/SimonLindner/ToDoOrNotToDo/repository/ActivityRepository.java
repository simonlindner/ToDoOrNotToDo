package SimonLindner.ToDoOrNotToDo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import SimonLindner.ToDoOrNotToDo.model.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
}

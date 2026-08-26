package SimonLindner.ToDoOrNotToDo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SimonLindner.ToDoOrNotToDo.service.ActivityService;
import SimonLindner.ToDoOrNotToDo.model.Activity;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService activityService;
    
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }
    
    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return activityService.saveActivity(activity);
    }
    
}

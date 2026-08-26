package SimonLindner.ToDoOrNotToDo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "min_temp")
    private Integer minTemp;

    @Column(name = "max_temp")
    private Integer maxTemp;

    @Column(name = "requires_sunlight")
    private Boolean requiresSunlight;

    @Column(name = "is_indoor")
    private Boolean isIndoor;
}

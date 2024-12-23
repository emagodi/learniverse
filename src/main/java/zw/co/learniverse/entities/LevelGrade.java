package zw.co.learniverse.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zw.co.learniverse.handlers.BaseEntity;


import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "school_id"}))
public class LevelGrade extends BaseEntity {

    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID levelGradeId;
    @NotNull
    private String name;
    private String description;
    @ElementCollection
   private List<GradingScale> gradingScales;

    @ManyToOne
    @JoinColumn(name = "school_id")
    @JsonIgnore
    private School school;

    public LevelGrade(){
        this.levelGradeId  = UUID.randomUUID();
    }
}

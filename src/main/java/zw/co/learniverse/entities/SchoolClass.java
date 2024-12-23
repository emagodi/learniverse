package zw.co.learniverse.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import zw.co.learniverse.handlers.BaseEntity;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name","school_id"}))
public class SchoolClass extends BaseEntity {

    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID schoolClassId;
    @NotNull
    private String name;
    @NotNull
    private UUID levelId;
    private String description;

    private UUID departmentId;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private School school ;

    @ElementCollection
    private Set<UUID> subjects = new HashSet<>();

}

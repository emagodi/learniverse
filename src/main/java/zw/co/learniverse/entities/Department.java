package zw.co.learniverse.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;


@Data
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Department{

    @Column(nullable = false, updatable = false)
    private UUID departmentId;
    @NotNull
    private String name;
    private String description;

    public Department() {
        this.departmentId = UUID.randomUUID(); // Generate UUID on creation
    }
}

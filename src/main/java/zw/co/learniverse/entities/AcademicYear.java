package zw.co.learniverse.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class AcademicYear {

    @Column(nullable = false, updatable = false)
    private UUID academicYearId;
    @NotNull
    private String name;

    private Date startDate;

    private Date endDate;

    public AcademicYear(){
        this.academicYearId  = UUID.randomUUID();
    }

}

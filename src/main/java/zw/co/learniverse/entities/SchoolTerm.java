package zw.co.learniverse.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zw.co.learniverse.enums.Status;


import java.util.Date;
import java.util.UUID;

@Data
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class SchoolTerm {

    @Column(nullable = false, updatable = false)
    private UUID schoolTermId;
    @NotNull
    private String name;

    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public SchoolTerm(){
        this.schoolTermId  = UUID.randomUUID();
    }

}

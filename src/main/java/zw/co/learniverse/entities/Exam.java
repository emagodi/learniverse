package zw.co.learniverse.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "examName"))
public class Exam {

    @Column(nullable = false, updatable = false)
    private UUID examId;

    private String examName;

    @Min(5)
    private Integer outOf;

    @Min(1)
    @Max(6)
    private Integer paperNo;

    private Double weight;

    @Future
    private LocalDateTime examDate;

    @Min(0)
    private Double examMark;
}

package zw.co.learniverse.entities;


import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "grade"))
public class GradingScale {

    private String grade;
    private double minScore;
    private double maxScore;
}

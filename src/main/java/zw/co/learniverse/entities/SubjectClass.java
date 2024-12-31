package zw.co.learniverse.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.learniverse.handlers.BaseEntity;


import java.time.Year;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"subjectId", "classId","termId","levelId","year"}))
public class SubjectClass extends BaseEntity {

    private UUID subjectId;
    private UUID classId;
    private UUID termId;
    private UUID levelId;

   private Year year;

    @ManyToMany(mappedBy = "subjectClasses", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonBackReference
    private List<Student> students;

    @ElementCollection
    private List<Exam> exams;

    private Double finalMark;

    private String comment;

    private String grade;

    public Double calculateFinalMark() {

        if (exams == null || exams.isEmpty()) {
            this.finalMark = 0.0;
            return  this.finalMark;
        }

        double totalWeightedMarks = 0.0;
        double totalWeight = 0.0;

        for (Exam exam : exams) {
            if (exam.getExamMark() != null && exam.getWeight() != null) {
                totalWeightedMarks += (exam.getExamMark() / exam.getOutOf()) * exam.getWeight();
                totalWeight += exam.getWeight();
            }
        }

        this.finalMark = totalWeight > 0 ? totalWeightedMarks : 0.0;

        return this.finalMark;
    }


    public void assignGrade(String gradeS) {
        this.grade = gradeS;
    }
}

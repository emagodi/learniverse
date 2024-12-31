package zw.co.learniverse.payload.response;


import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.learniverse.entities.Exam;


import java.time.Year;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectClassResponse {

    private UUID subjectId;
    private UUID classId;
    private UUID levelId;
    private Year year;
    private UUID termId;
    @ElementCollection
    private List<Exam> exams;
}

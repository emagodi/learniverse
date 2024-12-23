package zw.co.learniverse.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SubjectClassRequest {

    private Long schoolId;
    private UUID classId;
    private UUID subjectId;
    private UUID levelId;
    private UUID termId;


}

package zw.co.learniverse.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ExamAssignRequest {

    private Year year;

    private UUID levelId;

    private UUID termId;

    private UUID examId;

    private String examName;

    private Integer outOf;

    private Double weight;

    private Integer paperNo;

    private List<String> classIds;

    private LocalDateTime examDate;

    public static class ExamResponse {
    }
}
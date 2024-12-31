package zw.co.learniverse.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ExamResponse {

    private UUID examId;

    private String examName;

    private Integer outOf;

    private Integer paperNo;

    private Double weight;

    private LocalDateTime examDate;
}

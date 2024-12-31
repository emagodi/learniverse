package zw.co.learniverse.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportSubResponse {

    private Double finalMark;

    private String comment;

    private String grade;

    private UUID subjectId;
}

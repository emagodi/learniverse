package zw.co.learniverse.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.learniverse.entities.GradingScale;


import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LevelGradeRequest {

    private String name;
    private String description;
    private List<GradingScale> gradingScales;
}

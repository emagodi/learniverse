package zw.co.learniverse.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchoolClassRequest {

    @NotNull
    private String name;
    private String description;
    private String levelId;

}

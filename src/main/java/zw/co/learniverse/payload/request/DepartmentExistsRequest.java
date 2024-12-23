package zw.co.learniverse.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DepartmentExistsRequest {

    private Long schoolId;
    private UUID departmentId;
}

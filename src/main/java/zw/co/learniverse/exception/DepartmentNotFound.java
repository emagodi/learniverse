package zw.co.learniverse.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepartmentNotFound extends RuntimeException {
    private final String msg;
}

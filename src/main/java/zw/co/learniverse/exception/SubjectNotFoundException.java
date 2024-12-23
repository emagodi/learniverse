package zw.co.learniverse.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubjectNotFoundException extends RuntimeException{
    private final String msg;
}

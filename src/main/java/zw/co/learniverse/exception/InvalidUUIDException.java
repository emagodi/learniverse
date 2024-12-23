package zw.co.learniverse.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidUUIDException extends RuntimeException {
    private final String msg;
}

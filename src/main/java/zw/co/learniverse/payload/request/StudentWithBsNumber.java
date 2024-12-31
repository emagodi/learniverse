package zw.co.learniverse.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import zw.co.learniverse.entities.Student;


@Data
@AllArgsConstructor
public class StudentWithBsNumber {
    private Student student;
    private String accessNumber;

}
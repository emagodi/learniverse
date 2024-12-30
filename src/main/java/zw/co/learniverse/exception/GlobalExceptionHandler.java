package zw.co.learniverse.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;



import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LevelNotFoundException.class)
    public ResponseEntity<String> handleLevelNotFoundException(LevelNotFoundException exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMsg());
    }

    @ExceptionHandler(SchoolNotFoundException.class)
    public ResponseEntity<String> handleSchoolNotFoundException(SchoolNotFoundException exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMsg());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleConstraintViolationException(DataIntegrityViolationException ex) {
        // Customize the message based on your needs
        return "Constraint violation occurred: " + ex.getRootCause().getMessage();
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<String> handleSubjectNotFoundException(SubjectNotFoundException exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMsg());
    }

    @ExceptionHandler(TermNotFoundException.class)
    public ResponseEntity<String> handleTermNotFoundException(TermNotFoundException exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMsg());
    }

    @ExceptionHandler(DepartmentNotFound.class)
    public ResponseEntity<String> handleDepartmentNotFoundException(DepartmentNotFound exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMsg());
    }

//    @ExceptionHandler(SenderIdException.class)
//    public ResponseEntity<String> handleSenderIdException(SenderIdException exp){
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(exp.getMsg()
//                );
//    }

    @ExceptionHandler(InvalidUUIDException.class)
    public ResponseEntity<String> handleInvalidUUIDException(InvalidUUIDException exp){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exp.getMsg()
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exp){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exp.getMessage());
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleMethodArgException(MethodArgumentNotValidException exp){
//
//        var errors = new HashMap<String, String >();
//        exp.getBindingResult().getAllErrors()
//                .forEach(error -> {
//                    var fieldName = ((FieldError)error).getField();
//                    var errorMessage = error.getDefaultMessage();
//
//                    errors.put(fieldName, errorMessage);
//                });
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(new ErrorResponse(errors));
//    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        // Log only the message if needed
        System.out.println("Duplicate entry: " + ex.getMessage());

        // Create the error response
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}

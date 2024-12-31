package zw.co.learniverse.payload.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.enums.Status;


import java.util.Date;
import java.util.UUID;




@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentRequest{


    private String firstname;
    private String middlename;
    private String lastname;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    private String email;
    private String phonenumber;
    private String address;
    private String district;
    private String province;
    private String parentEmail;
    private String parentPhone;
    private String country;
    private String bloodGroup;
    private Long schoolId;
    private UUID levelId;
    private UUID classId;
    private UUID termId;

    //@JsonIgnore
    @Schema(description = "Profile picture of the student", type = "string", format = "binary")
    private MultipartFile picture;


    @Enumerated(EnumType.STRING)
    private Status status;


    private String level;
    private String className;

}

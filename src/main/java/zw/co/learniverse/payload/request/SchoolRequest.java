package zw.co.learniverse.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.enums.OwnershipType;
import zw.co.learniverse.enums.PaymentMethod;
import zw.co.learniverse.enums.Status;

import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SchoolRequest {

    private String name;
    //private String bsNumber;
    private String town;
    private String streetName;
    private String district;
    private String province;
    private String region;
    private String country;

    private String schoolContact;
    private String schoolEmail;
    private String headFirstname;
    private String headLastname;
    private String headEmail;
    private String headPhonenumber;

    private String adminFirstname;
    private String adminLastName;
    private String adminEmail;
    private String adminPhonenumber;

    private Integer numberOfStudents;
    private Integer numberOfTeachers;
    private String adminNote;
    private String responsibleAuthority;
    @Enumerated(EnumType.STRING)
    private OwnershipType ownershipType;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String paymentReferenceNumber;

    //@JsonIgnore
    @Schema(description = "Profile picture of the student", type = "string", format = "binary")
    private MultipartFile receipt;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    //@JsonIgnore
    @Schema(description = "School logo", type = "string", format = "binary")
    private MultipartFile logo;

    private String adminPassword;


}

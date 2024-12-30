package zw.co.learniverse.payload.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.learniverse.entities.*;
import zw.co.learniverse.enums.OwnershipType;
import zw.co.learniverse.enums.PaymentMethod;
import zw.co.learniverse.enums.Status;


import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolResponse {

    private Long id;
    private String name;
    private String bsNumber;
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


    private String receiptUrl;

    private String paymentReferenceNumber;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String logoUrl;
    private Status status;

    private List<Department> departments;

    private List<SchoolClass> schoolClass;

    private List<LevelGrade> levelGrades;

    private List<Subjects> subjects;

    private List<SchoolTerm> schoolTerms;


    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private String message;




}

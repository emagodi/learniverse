package zw.co.learniverse.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.learniverse.enums.Status;
import zw.co.learniverse.handlers.BaseEntity;


import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
@EntityListeners(AuditingEntityListener.class)
public class Student extends BaseEntity {


    private String firstname;

    private String lastname;

    private String middlename;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long schoolId;

    private Date dob;

    //    @Pattern(regexp = "^\\+?[0-9. ()-]{7,}$", message = "Invalid phone number format!")
    private String phonenumber;

    @Email
    private String email;

   private String address;

   private String district;

   private String accessNumber;

   private String province;

    private String country;

    private String bloodGroup;

    private String term;

    private Year year;

    private String level;

    @Email
    private String parentEmail;

    @NotNull
    private UUID regParent;

    private String parentPhone;

    private String picture;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID classId;

    @ManyToMany
    @JoinTable(
            name = "student_subject_class",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_class_id")
    )
    @JsonManagedReference
    private List<SubjectClass> subjectClasses;

    private UUID levelId;
    private UUID termId;


}

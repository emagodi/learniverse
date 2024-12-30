package zw.co.learniverse.entities;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zw.co.learniverse.enums.OwnershipType;
import zw.co.learniverse.enums.PaymentMethod;
import zw.co.learniverse.enums.Status;
import zw.co.learniverse.handlers.BaseEntity;


import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schools")
@EntityListeners(AuditingEntityListener.class)
public class School extends BaseEntity {


    private String name;
    private String schoolNumber;
    private String streetName;
    private String town;
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

    @Enumerated(EnumType.STRING)
    private OwnershipType ownershipType;

    private String responsibleAuthority;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String paymentReferenceNumber;

    private String receipt;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String logo;

    @ElementCollection
    @CollectionTable(name = "departments", joinColumns = @JoinColumn(name = "school_id"))
    private List<Department> departments = new ArrayList<>();


    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SchoolClass> schoolClass = new ArrayList<>();

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private List<LevelGrade> levelGrade = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "subjects", joinColumns = @JoinColumn(name = "school_id"))
    private List<Subjects> subjects = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "terms", joinColumns = @JoinColumn(name = "school_id"))
    private List<SchoolTerm> schoolTerms = new ArrayList<>();


}

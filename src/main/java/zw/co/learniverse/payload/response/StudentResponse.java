package zw.co.learniverse.payload.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.learniverse.enums.Status;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {

    private Long id;
    private String firstname;
    private String lastname;
    private String middlename;
    private Date dob;
    private String email;
    private String phonenumber;
    private String parentPhone;
    private String address;
    private String district;
    private String province;
    private String parentEmail;
    private String country;
    private String bloodGroup;
    private Long schoolId;
    private UUID classId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private UUID levelId;
    private UUID termId;
    private UUID regParent;

}

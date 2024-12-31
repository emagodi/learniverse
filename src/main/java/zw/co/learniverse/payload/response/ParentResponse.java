package zw.co.learniverse.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

 private String phonenumber;

    private String whatsappNumber;

    private String email;

    private String address;

    private String district;

    private String province;

    private String country;

    private Long userId;

    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}


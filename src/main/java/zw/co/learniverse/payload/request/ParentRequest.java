package zw.co.learniverse.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ParentRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String middleName;

    private String phonenumber;

    private String whatsappNumber;

    @Email
    private String email;

    private String address;

    private String district;

    private String province;

    private String password;

    private String country;
}

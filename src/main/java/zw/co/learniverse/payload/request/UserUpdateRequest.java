package zw.co.learniverse.payload.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zw.co.learniverse.enums.Role;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "Firstname field can't be blank")
    private String firstname;

    private String lastname;

    @NotBlank(message = "Email field can't be blank")
    @Column(unique = true)
    @Email(message = "Please enter proper email format!")
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;


    private Long schoolId;

    @Column(unique = true)
    private String accessNumber;

}

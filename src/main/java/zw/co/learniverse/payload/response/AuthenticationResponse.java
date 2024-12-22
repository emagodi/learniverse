package zw.co.learniverse.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private  Long id;

    private String firstname;

    private String lastname;

    private String password;


    private String email;

    private List<String> roles;

    private Long schoolId;

    @Column(unique = true)
    private String accessNumber;


    private boolean temporaryPassword;

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("token_type")
    private String tokenType;

    private String message;

    private boolean createdByAdmin;

}

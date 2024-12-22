package zw.co.learniverse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import zw.co.learniverse.entities.User;
import zw.co.learniverse.exception.AuthenticationException;
import zw.co.learniverse.payload.request.AuthenticationRequest;
import zw.co.learniverse.payload.request.RefreshTokenRequest;
import zw.co.learniverse.payload.request.RegisterRequest;
import zw.co.learniverse.payload.request.UserUpdateRequest;
import zw.co.learniverse.payload.response.AuthenticationResponse;
import zw.co.learniverse.payload.response.RefreshTokenResponse;
import zw.co.learniverse.service.AuthenticationService;
import zw.co.learniverse.service.JwtService;
import zw.co.learniverse.service.RefreshTokenService;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Tag(name = "AUTHENTICATION", description = "The Authentication APIs. Contains operations like login, logout, refresh-token etc.")
@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirements() /*
This API won't have any security requirements. Therefore, we need to override the default security requirement configuration
with @SecurityRequirements()
*/
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Register New User",
            description = "Create new user by posting firstname, lastname, email, password, role, etc. ")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                      @RequestParam boolean createdByAdmin,
                                      @RequestHeader Map<String, String> headers) {
        String authorizationValue = null;

        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
            authorizationValue = headers.get("authorization").substring(7);
        }
        System.out.println(createdByAdmin);
        try {
            AuthenticationResponse authenticationResponse = authenticationService.register(request, createdByAdmin, authorizationValue);

            ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
            ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(authenticationResponse); // Return the authentication response
        } catch (DataIntegrityViolationException e) {
            String message = extractDuplicateEntryMessage(e.getMessage());
            if (message != null) {
                // Return Conflict status with a plain string message
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Email and or accessNumber already exists: " + message); // Return the duplicate entry message
            }
            // Handle other DataIntegrityViolationException cases if necessary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage()); // Return error message as a string
        }
    }

    @PostMapping("/authenticate")
    @Operation(
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")}
                    )
            }
    )

    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        log.info("Received authentication request for email: {}", request.getEmail());

        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
            ResponseCookie jwtCookie = jwtService.generateJwtCookie(authenticationResponse.getAccessToken());
            ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authenticationResponse.getRefreshToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(authenticationResponse);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(request));
    }

    @PostMapping("/refresh-token-cookie")
    public ResponseEntity<Void> refreshTokenCookie(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
        RefreshTokenResponse refreshTokenResponse = refreshTokenService
                .generateNewToken(new RefreshTokenRequest(refreshToken));
        ResponseCookie NewJwtCookie = jwtService.generateJwtCookie(refreshTokenResponse.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, NewJwtCookie.toString())
                .build();
    }
    @GetMapping("/info")
    public Authentication getAuthentication(@RequestBody AuthenticationRequest request){
        return     authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
    }
    @PostMapping("/logout")
    @Operation(summary = "Logout",
            description = "End point to logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);

        if (refreshToken != null) {
            try {
                refreshTokenService.deleteByToken(refreshToken);
                log.info("Successfully deleted refresh token: {}", refreshToken);
            } catch (Exception e) {
                log.error("Error deleting refresh token: {}", refreshToken, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            log.warn("No refresh token found in cookies for logout");
        }

        ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = refreshTokenService.getCleanRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }

    @GetMapping("user/id/{id}")
    @Operation(summary = "Find User By User Id",
            description = "This endpoint will allow you to get a particular user by their user id.")
    // @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN' , 'SUPERADMIN', 'TEACHER')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = authenticationService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user); // Return 200 OK with user entity
        } else {
            String notFoundMessage = "User not found for ID: " + id;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundMessage); // Return 404 Not Found with message
        }
    }
    @PutMapping("update/id/{userId}")
    @Operation(summary = "Endpoint to update user by id",
            description = "This endpoint will allow you to update user by id. If you are updating to an already existing user it will show email already exists message")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequest userUpdateRequest) {
        System.out.println(userUpdateRequest);
        try {
            User updatedUser = authenticationService.updateUser(userId, userUpdateRequest);
            return ResponseEntity.ok(updatedUser); // Return the updated User entity
        } catch (DataIntegrityViolationException e) {
            String message = extractDuplicateEntryMessage(e.getMessage());
            if (message != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(message); // Return the extracted duplicate entry message
            }
            // Handle other DataIntegrityViolationException cases if necessary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage()); // Include original message for debugging
        }
    }

    // Extract duplicate entry message using regex
    private String extractDuplicateEntryMessage(String errorMessage) {
        String regex = "Duplicate entry '([^']+)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            return " " + matcher.group(0); // Return the matched duplicate entry message
        }
        return null; // Return null if no match is found
    }

    @PostMapping("/change-password/{email}/{currentPassword}/{newPassword}")
    @Operation(summary = "Endpoint to change password",
            description = "This endpoint will allow you to change password by entering old password and new password. If the two are different, error is displayed")
    public ResponseEntity<String> changePassword(
            @PathVariable String email,
            @PathVariable String currentPassword,
            @PathVariable String newPassword) {
        try {
            authenticationService.changePassword(email, currentPassword, newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            // Return the message for invalid old password
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle the specific case for invalid old password
            return ResponseEntity.badRequest().body("Invalid old password.");
        }
    }

}

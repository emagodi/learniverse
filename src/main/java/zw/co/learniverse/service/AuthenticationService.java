package zw.co.learniverse.service;

import zw.co.learniverse.entities.User;
import zw.co.learniverse.payload.request.AuthenticationRequest;
import zw.co.learniverse.payload.request.RegisterRequest;
import zw.co.learniverse.payload.request.UserUpdateRequest;
import zw.co.learniverse.payload.response.AuthenticationResponse;


public interface AuthenticationService {

    AuthenticationResponse register(RegisterRequest request, boolean createdByAdmin, String token);
    AuthenticationResponse authenticate(AuthenticationRequest request);

    public User getUserById(Long id);


    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    public void changePassword(String email, String currentPassword, String newPassword);

}

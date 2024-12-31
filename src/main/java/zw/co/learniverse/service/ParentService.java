package zw.co.learniverse.service;

import org.springframework.http.ResponseEntity;
import zw.co.learniverse.payload.request.ParentRequest;
import zw.co.learniverse.payload.response.ParentResponse;
import zw.co.learniverse.payload.response.StudentResponse;


import java.util.List;

public interface ParentService {
    ParentResponse getParentByUserId(Long userId);

    ParentResponse getParentByEmail(String email);

    ResponseEntity<String> createParent(ParentRequest parentRequest, String regParent, String phoneNo);

    List<StudentResponse> getMyKids(String parentEmail);
}

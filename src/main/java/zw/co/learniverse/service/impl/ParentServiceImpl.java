//package zw.co.learniverse.service.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import tech.brainstake.studentservice.entities.Parent;
//import tech.brainstake.studentservice.payload.request.ParentRequest;
//import tech.brainstake.studentservice.payload.request.UserRequest;
//import tech.brainstake.studentservice.payload.response.ParentResponse;
//import tech.brainstake.studentservice.payload.response.StudentResponse;
//import tech.brainstake.studentservice.payload.response.UserResponse;
//import tech.brainstake.studentservice.repository.ParentRepository;
//import tech.brainstake.studentservice.service.ParentService;
//import tech.brainstake.studentservice.service.StudentService;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ParentServiceImpl implements ParentService {
//
//    private final RestTemplate restTemplate;
//
//    private final ParentRepository parentRepository;
//
//    private final StudentService studentService;
//
//
//    @Value("${user.service.url}")
//    private String userServiceUrl;
//
//    @Value("${api.gateway.url}") // Set the API Gateway URL
//    private String apiGatewayUrl;
//
//    @Override
//    public ParentResponse getParentByUserId(Long userId) {
//        Parent parent = parentRepository.findParentByUserId(userId);
//        return mapToParentResponse(parent);
//    }
//
//
//    @Override
//    public ParentResponse getParentByEmail(String email){
//        Parent parent = parentRepository.findByEmail(email);
//
//        return mapToParentResponse(parent);
//    }
//
//    @Override
//    public ResponseEntity<String> createParent(ParentRequest parentRequest, String regParent, String phoneNo){
//
//        if (!studentService.parentExistsByPhoneAndUUID(phoneNo, regParent)){
//            return ResponseEntity.badRequest().body("You cant create a parent without a student enrolled");
//        }
//        UserRequest userRequest = new UserRequest(
//                parentRequest.getFirstName(),
//                parentRequest.getLastName(),
//                parentRequest.getEmail(),
//                parentRequest.getPassword(),
//                "USER"
//        );
//
//
//        // Send user data to user-service
//        ResponseEntity<UserResponse> userResponse = restTemplate.postForEntity(
//                userServiceUrl + "/api/v1/auth/register?createdByAdmin=true",
//                userRequest,
//                UserResponse.class
//        );
//
//        // Extract user ID from the response
//        Long userId = userResponse.getBody().getId(); // Get the user ID
//
//        Parent parent = Parent.builder()
//                .middleName(parentRequest.getMiddleName())
//                .phonenumber(parentRequest.getPhonenumber())
//                .province(parentRequest.getProvince())
//                .district(parentRequest.getDistrict())
//                .address(parentRequest.getAddress())
//                .country(parentRequest.getCountry())
//                .whatsappNumber(parentRequest.getWhatsappNumber())
//                .email(parentRequest.getEmail())
//                .lastName(parentRequest.getLastName())
//                .firstName(parentRequest.getFirstName())
//                .userId(userId)
//                .build();
//
//       parentRepository.save(parent);
//
//        return ResponseEntity.ok("Parent created successfully");
//    }
//
//    @Override
//    public List<StudentResponse> getMyKids(String parentEmail) {
//        return studentService.findStudentsByParentEmail(parentEmail);
//    }
//
//
//    private ParentResponse mapToParentResponse(Parent parent){
//
//        return ParentResponse.builder()
//                .id(parent.getId())
//                .district(parent.getDistrict())
//                .email(parent.getEmail())
//                .firstName(parent.getFirstName())
//                .lastName(parent.getLastName())
//                .createdBy(parent.getCreatedBy())
//                .createdAt(parent.getCreatedAt())
//                .updatedAt(parent.getUpdatedAt())
//                .updatedBy(parent.getUpdatedBy())
//                .userId(parent.getUserId())
//                .phonenumber(parent.getPhonenumber())
//                .whatsappNumber(parent.getWhatsappNumber())
//                .country(parent.getCountry())
//                .province(parent.getProvince())
//                .address(parent.getAddress())
//                .middleName(parent.getMiddleName())
//                .build();
//    }
//}
//
//
//

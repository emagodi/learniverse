//package zw.co.learniverse.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import tech.brainstake.studentservice.payload.request.ParentRequest;
//import tech.brainstake.studentservice.payload.response.ParentResponse;
//import tech.brainstake.studentservice.payload.response.StudentResponse;
//import tech.brainstake.studentservice.service.ParentService;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/parent")
//public class ParentController {
//
//    private final ParentService parentService;
//
//    @Value("${user.service.url}")
//    private String userServiceUrl;
//
//    @GetMapping("/{userId}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public ParentResponse getParentById(@PathVariable("userId") Long userId){
//        return parentService.getParentByUserId(userId);
//    }
//
//    @PostMapping("/{regParent}/{phoneNo}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<String> createParent(@Valid @RequestBody ParentRequest parentRequest,
//                                               @PathVariable("regParent") String regParent,
//                                               @PathVariable("phoneNo") String phoneNo
//                                               ){
//        return parentService.createParent(parentRequest, regParent, phoneNo);
//    }
//
//    @GetMapping("/kids/{parentEmail}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public List<StudentResponse> getMyStudents(@PathVariable("parentEmail") String parentEmail){
//        return parentService.getMyKids(parentEmail);
//    }
//
//}

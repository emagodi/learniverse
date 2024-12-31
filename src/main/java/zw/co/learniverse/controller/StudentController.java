package zw.co.learniverse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.entities.Student;
import zw.co.learniverse.payload.request.StudentRequest;
import zw.co.learniverse.payload.response.ApiResponse;
import zw.co.learniverse.payload.response.StudentResponse;
import zw.co.learniverse.service.StudentService;


import java.io.IOException;
import java.time.Year;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/students")
public class StudentController {


    private final StudentService studentService;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN')")
    @Operation(summary = "Create New Student", description = "Create a new student by uploading their information and picture.")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Parameter(description = "Student information including picture")
            @ModelAttribute StudentRequest studentRequest,
            @RequestHeader Map<String, String> headers) throws IOException {

        log.info("Submitting Student");

        // Extract the authorization token
        String authorizationValue = null;
        if (headers.containsKey("authorization") && headers.get("authorization").length() > 7) {
            authorizationValue = headers.get("authorization").substring(7);
        }

        // Call the service to save the student entity
        Student savedStudent = studentService.createStudent(studentRequest, authorizationValue);

        // Map the saved student entity to StudentResponse
        StudentResponse studentResponse = studentService.mapToStudentResponse(savedStudent);

        // Create the response object
        ApiResponse<StudentResponse> response = new ApiResponse<>("Student created successfully", studentResponse);

        // Return the response entity with status and body
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


//    @GetMapping("/all")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER')")
//    @ResponseStatus(HttpStatus.OK)
//    public List<StudentResponse> getAllStudents(){
//
//        return studentService.getAllStudents();
//    }
//
//    @GetMapping("/email/{email}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'STUDENT', 'USER','TEACHER')")
//    @ResponseStatus(HttpStatus.OK)
//    public StudentResponse getStudentByEmail(
//            @PathVariable("email") String email
//    ){
//
//        return studentService.getStudentByEmail(email);
//    }
//
//    @GetMapping("school/{schoolId}/level/{levelId}/class/{classId}/term/{termId}/year/{year}/subject/{subjectId}/list")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public List<StudentResponse> getAllStudentsInSubjectClass(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("classId") String classId,
//            @PathVariable("termId") String termId,
//            @PathVariable("year") Year year,
//            @PathVariable("subjectId") String subjectId
//    ){
//        return studentService.getAllStudentsInSubjectClass(schoolId,levelId, classId, termId, year, subjectId);
//    }
//
//    @GetMapping("school/{schoolId}/level/{levelId}/class/{classId}/term/{termId}/year/{year}/list")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public List<StudentResponse> getAllStudentsInClass(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("classId") String classId,
//            @PathVariable("termId") String termId,
//            @PathVariable("year") Year year
//    ){
//        return studentService.getAllStudentsInClass(schoolId,levelId, classId, termId, year);
//    }
//
//    @GetMapping("/sb")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public List<SubjectClass> getAll(){
//        return studentService.getAll();
//    }
//
//
//
//    @GetMapping("/school/{schoolId}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN','TEACHER')")
//    @ResponseStatus(HttpStatus.OK)
//    public List<StudentResponse> getAllStudentsBySchool( @PathVariable("schoolId") Long id
//    ){
//        return studentService.getAllStudentsBySchool(id);
//    }
//
//    @GetMapping("/{studentId}/get")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN','TEACHER')")
//    @ResponseStatus(HttpStatus.OK)
//    public StudentResponse getById(
//            @PathVariable("studentId") Long studentId
//    ){
//        return studentService.findById(studentId);
//    }
//
//    @GetMapping("/{studentId}/sb")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public Optional<Student> getAllById(@PathVariable("studentId") Long studentId){
//        return studentService.getStudentWithSubjectsClass(studentId);
//    }
//
//    //  Long studentId,
//    //            String subjectId,
//    //            String classId,
//    //            String levelId,
//    //            String termId,
//    //            Year year,
//    //            String examId,
//    //            Double mark
//    @PutMapping("/{studentId}/class/{classId}/subject/{subjectId}/level/{levelId}/term/{termId}/year/{year}/exam/{examId}/mark")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public Exam markExam(
//            @PathVariable("studentId") Long studentId,
//            @PathVariable("classId") String classId,
//            @PathVariable("subjectId") String subjectId,
//            @PathVariable("examId") String examId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("termId") String termId,
//            @PathVariable("year") Year year,
//            @RequestBody Double examMark,
//            @RequestHeader Map<String, String> headers
//    ){
//        String authorizationValue = null;
//
//
//        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
//            authorizationValue = headers.get("authorization").substring(7);
//        }
//        return studentService.markExamForStudent(studentId,subjectId,classId,levelId,termId,year,examId, examMark, authorizationValue);
//    }
//
//    //apiGatewayUrl + "/api/v1/schools/"+existingStudent.getSchoolId()+"/level/"+levelId+"/class/"+classId+"/exists"
//    @PutMapping("/{studentId}/level/{levelId}/class/{classId}/term/{termId}/year/{year}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public String assignStudentToClass(
//            @PathVariable("studentId") Long studentId,
//            @PathVariable("classId") String classId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("termId") String termId,
//            @PathVariable("year") Year year,
//    @RequestHeader Map<String, String> headers) throws ClassNotFoundException {
//        String authorizationValue = null;
//
//
//        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
//            authorizationValue = headers.get("authorization").substring(7);
//        }
//        // (Long studentId, String classId,String levelId, String authorizationValue)
//        return studentService.assignToClass(studentId,classId,levelId,termId,year,authorizationValue);
//    }
//
//    @PutMapping("/{studentId}/schools/{schoolId}/level/{levelId}/class/{classId}/term/{termId}/year/{year}/comment")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public String putComment(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("studentId") Long studentId,
//            @PathVariable("classId") String classId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("termId") String termId,
//            @PathVariable("year") Year year,
//            @RequestBody SubjectComment subjectComment
//           )  {
//      return  studentService.putCommentToStudent(schoolId,
//               studentId,
//             classId,
//                subjectComment.getSubjectId(),
//                termId,
//                 levelId,
//                year,
//              subjectComment.getComment()
//
//        );
//    }
//
//    @GetMapping("/{studentId}/schools/{schoolId}/level/{levelId}/class/{classId}/term/{termId}/year/{year}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public ReportCardResponse generateReportForStudent(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("studentId") Long studentId,
//            @PathVariable("classId") String classId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("termId") String termId,
//            @PathVariable("year") Year year) {
//       return studentService.generateReportCardForStudent(schoolId,
//                 studentId,
//                 classId,
//                termId,
//                levelId,
//                year
//        );
//    }
//
//    @GetMapping("/schools/{schoolId}/level/{levelId}/class/{classId}/term/{termId}/year/{year}/total")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public int totalStudentInClass(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("classId") String classId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("termId") String termId,
//            @PathVariable("year") Year year,
//            @RequestHeader Map<String, String> headers) {
//        String authorizationValue = null;
//
//
//        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
//            authorizationValue = headers.get("authorization").substring(7);
//        }
//
//        // (Long studentId, String classId,String levelId, String authorizationValue)
//        return studentService.countStudentsInClass(schoolId,classId,levelId,termId,year, authorizationValue);
//    }
//
//    // /api/v1/students/schools/"+ teacher.getSchoolId() + "/class/"+ examAssignRequest.getClassId() + "/subject/"+ examAssignRequest.getSubjectId()+"/set_exam"
//    @PutMapping("/schools/{schoolId}/class/{classId}/subject/{subjectId}/set_exam")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public String assignExamToClass(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("classId") String classId,
//            @PathVariable("subjectId") String subjectId,
//            @RequestBody ExamAssignRequest examAssignRequest
//            ) {
//        System.out.println("Endpoint hit");
//
//        return studentService.assignExamToClass(schoolId,classId,subjectId,examAssignRequest);
//    }
//
//    // set exam for a list of classes
//    @PutMapping("/schools/{schoolId}/subject/{subjectId}/classes/set_exam")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public String assignExamToClasses(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("subjectId") String subjectId,
//            @RequestBody ExamAssignRequest examAssignRequest
//    ) {
//        System.out.println("Endpoint hit");
//
//        return studentService.assignExamToClasses(schoolId,examAssignRequest.getClassIds(),subjectId,examAssignRequest);
//    }
//
//    @GetMapping("/{parentEmail}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    public List<StudentResponse> getAllStudentsByParentEmail(@PathVariable("parentEmail") String parentEmail){
//        return studentService.getAllStudentsByParentEmail(parentEmail);
//    }
//
//    @GetMapping("/user")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    public String get(@RequestHeader("loggedInUser") String username){
//        return username;
//    }
//
//    @GetMapping("/headers")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN')")
//    public String getHeaders(@RequestHeader Map<String, String> headers) {
//        StringBuilder headerInfo = new StringBuilder();
//        headers.forEach((key, value) -> headerInfo.append(key).append(": ").append(value).append("\n"));
//        return headerInfo.toString();
//    }
//
//    @PutMapping("/update/{studentId}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    public StudentResponse updateStudent(
//            @PathVariable("studentId") Long studentId,
//            @RequestBody StudentUpdateRequest studentUpdateRequest) {
//        return studentService.updateStudent(studentId, studentUpdateRequest);
//    }
//
//    @PutMapping("/{studentId}/assign/subjectclass")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    public List<SubjectClass> assignSubjectToStudent(
//            @PathVariable("studentId") Long studentId,
//            @RequestBody SubjectClassRequest subjectClassRequest,
//            @RequestHeader Map<String, String> headers) {
//        String authorizationValue = null;
//
//        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
//            authorizationValue = headers.get("authorization").substring(7);
//        }
//        return studentService.assignSubject(studentId, subjectClassRequest,authorizationValue);
//    }
//
//
//    @PostMapping(value = "/upload", consumes = "multipart/form-data")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN')")
//    @Operation(summary = "Upload CSV file for students",
//            description = "This endpoint allows uploading a CSV file to create students.")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {
//        if (file.isEmpty()) {
//            return "Please provide a valid CSV file.";
//        }
//
//        studentService.uploadCsv(file);
//        return "CSV file uploaded successfully, students created in the database.";
//    }
//
//
//    @GetMapping("/search/")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    public ResponseEntity<List<StudentResponse>> filterStudents(
//            @RequestParam(required = false) String firstName,
//            @RequestParam(required = false) String lastName,
//            @RequestParam(required = false) String email,
//            @RequestParam(required = false) Status status) {
//
//        List<StudentResponse> filteredStudents = studentService.findStudents(firstName, lastName, email, status);
//        return ResponseEntity.ok(filteredStudents);
//    }
//
//
//    @PostMapping(value = "excel/upload", consumes = "multipart/form-data")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN')")
//    @Operation(summary = "Upload Excel file for students",
//            description = "This endpoint allows uploading an Excel file to create students.")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String uploadExcel(@RequestParam("file") MultipartFile file,
//                              @RequestHeader Map<String, String> headers) throws IOException {
//
//        String authorizationValue = null;
//
//        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
//            authorizationValue = headers.get("authorization").substring(7);
//        }
//        if (file.isEmpty()) {
//            return "Please provide a valid Excel file.";
//        }
//
//        studentService.uploadExcelFile(file, authorizationValue);
//        return "Excel file uploaded successfully, students created in the database.";
//    }
//
//    @PostMapping(value = "excel/class/upload", consumes = "multipart/form-data")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN')")
//    @Operation(summary = "Upload Excel file for students per class",
//            description = "This endpoint allows uploading an Excel file to create students by class.")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String uploadExcelClass(@RequestParam("file") MultipartFile file,
//                              @RequestHeader Map<String, String> headers) throws IOException {
//
//        String authorizationValue = null;
//
//        if (headers.get("authorization") != null && headers.get("authorization").length() > 7) {
//            authorizationValue = headers.get("authorization").substring(7);
//        }
//        if (file.isEmpty()) {
//            return "Please provide a valid Excel file.";
//        }
//
//        studentService.uploadExcelFileClass(file, authorizationValue);
//        return "Excel file uploaded successfully, students created in the database.";
//    }
//


}

package zw.co.learniverse.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.entities.Student;
import zw.co.learniverse.entities.User;
import zw.co.learniverse.enums.Role;
import zw.co.learniverse.exception.DuplicateEmailException;
import zw.co.learniverse.payload.request.MailBody;
import zw.co.learniverse.payload.request.StudentRequest;
import zw.co.learniverse.payload.response.StudentResponse;
import zw.co.learniverse.repository.StudentRepository;
import zw.co.learniverse.repository.SubjectClassRepository;
import zw.co.learniverse.repository.UserRepository;
import zw.co.learniverse.service.EmailService;
import zw.co.learniverse.service.JwtService;
import zw.co.learniverse.service.StudentService;
import zw.co.learniverse.exception.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;


import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Year;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SubjectClassRepository subjectClassRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${file.upload.directory}")
    private String uploadDir;

    @Transactional
    public Student createStudent(StudentRequest studentRequest, String token) throws IOException {
        // Generate a random password
        String generatedPassword = generateRandomPassword(12); // Adjust length as needed
        Long schoolId;

        // Extract schoolId from admin if logged in
        if (token != null) {
            String username = jwtService.extractUserName(token);
            User admin = userRepository.findByEmail(username)
                    .orElseThrow(() -> new AuthenticationException("Admin not found."));
            schoolId = admin.getSchoolId(); // Get the schoolId from the admin
        } else {
            schoolId = studentRequest.getSchoolId(); // Use provided schoolId if not created by admin
        }

        // Create the Student object
        Student student = Student.builder()
                .firstname(studentRequest.getFirstname())
                .lastname(studentRequest.getLastname())
                .middlename(studentRequest.getMiddlename())
                .dob(studentRequest.getDob())
                .email(studentRequest.getEmail())
                .phonenumber(studentRequest.getPhonenumber())
                .address(studentRequest.getAddress())
                .district(studentRequest.getDistrict())
                .province(studentRequest.getProvince())
                .parentEmail(studentRequest.getParentEmail())
                .parentPhone(studentRequest.getParentPhone())
                .country(studentRequest.getCountry())
                .bloodGroup(studentRequest.getBloodGroup())
                .schoolId(schoolId) // Use the schoolId obtained
                .levelId(studentRequest.getLevelId())
                .classId(studentRequest.getClassId())
                .termId(studentRequest.getTermId())
                .status(studentRequest.getStatus())
                .level(studentRequest.getLevel())
                .regParent(UUID.randomUUID())
                .build();

        // Handle profile picture upload if present
        if (studentRequest.getPicture() != null && !studentRequest.getPicture().isEmpty()) {
            MultipartFile pictureFile = studentRequest.getPicture();
            String pictureUrl = uploadFileToLocalFolder(pictureFile, "profiles");
            student.setPicture(pictureUrl); // Assuming Student entity has a field for picture URL
        }

        // Save the student entity to the repository
        Student savedStudent = studentRepository.save(student);

        // Create the user object
        User studentUser = User.builder()
                .firstname(studentRequest.getFirstname())
                .lastname(studentRequest.getLastname())
                .email(studentRequest.getEmail())
                .password(passwordEncoder.encode(generatedPassword)) // Hash password
                .role(Role.STUDENT) // Set the appropriate role
                .schoolId(schoolId) // Set the school ID
                .temporaryPassword(true) // Set if needed
                .build();

        try {
            // Save the student user
            studentUser = userRepository.save(studentUser);

            // Update the student with the generated userId
            savedStudent.setUserId(studentUser.getId()); // Assuming Student has a userId field
            studentRepository.save(savedStudent); // Save the updated Student entity
        } catch (DataIntegrityViolationException e) {
            // Handle duplicate email error
            throw new DuplicateEmailException("Email already exists: " + studentUser.getEmail());
        }

        // Prepare email content
        String subject = "Your Student Account Has Been Created";
        String body = "Your account has been successfully created. Your password is: " + generatedPassword;

        System.out.println(generatedPassword);

        // Create a MailBody object
        MailBody mailBody = new MailBody(studentUser.getEmail(), subject, body);

        // Send email with the generated password
        emailService.sendSimpleMessage(mailBody);

        // Return the saved student
        return savedStudent; // Return the saved Student entity
    }

    private String uploadFileToLocalFolder(MultipartFile file, String folder) throws io.jsonwebtoken.io.IOException, java.io.IOException {
        // Check if the file is null or empty
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Define the full path for the upload directory
        String fullUploadPath = uploadDir + "/" + folder;
        System.out.println("Upload path: " + fullUploadPath); // Debug logging

        // Create the directory if it doesn't exist
        File directory = new File(fullUploadPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs(); // Attempt to create the directory
            if (!created) {
                throw new io.jsonwebtoken.io.IOException("Failed to create directory: " + fullUploadPath);
            }
        }

        // Generate a unique filename
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File destinationFile = new File(directory, filename);

        // Save the file to the local directory
        file.transferTo(destinationFile);

        // Return the URL where the file can be accessed
        return uploadDir + "/" + folder + "/" + filename; // Adjust as needed
    }

    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }


    public StudentResponse mapToStudentResponse(Student student) {

        return StudentResponse.builder()
                .id(student.getId())
                .email(student.getEmail())
                .firstname(student.getFirstname())
                .lastname(student.getLastname())
                .phonenumber(student.getPhonenumber())
                .country(student.getCountry())
                .middlename(student.getMiddlename())
                .address(student.getAddress())
                .parentEmail(student.getParentEmail())
                .parentPhone(student.getParentPhone())
                .district(student.getDistrict())
                .province(student.getProvince())
                .schoolId(student.getSchoolId())
                .classId(student.getClassId())
                .userId(student.getUserId())
                .bloodGroup(student.getBloodGroup())
                .dob(student.getDob())
                .regParent(student.getRegParent())
                .status(student.getStatus())
                .createdAt(student.getCreatedAt())
                .createdBy(student.getCreatedBy())
                .updatedAt(student.getUpdatedAt())
                .levelId(student.getLevelId())
                .termId(student.getTermId())
                .regParent(student.getRegParent())
                .updatedBy(student.getUpdatedBy())
                .build();
    }


//    public List<StudentResponse> getAllStudents() {
//        System.out.println("trying here");
//        List<Student> students = studentRepository.findAll();
//
//        return students.stream()
//                .map(this::mapToStudentResponse)
//                .sorted(Comparator.comparing(StudentResponse::getId).reversed())
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public StudentResponse getStudentByEmail(String email){
//        Student student = studentRepository.findStudentByEmail(email).stream().findFirst().orElseThrow(() -> new StudentNotFoundException("Student with specified email not found"));
//
//        return mapToStudentResponse(student);
//    }
//
//    @Override
//    public List<SubjectClass> getAll(){
//        return subjectClassRepository.findAll();
//    }
//
//    @Transactional
//    @Override
//    public String assignToClass(Long studentId, String classId,String levelId,String termId, Year year, String authorizationValue) throws ClassNotFoundException {
//        Student existingStudent = studentRepository.findById(studentId)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
//
//        HttpHeaders headers = new HttpHeaders();
//
//
//        headers.setBearerAuth(authorizationValue);
//        HttpEntity<Object> entity = new HttpEntity<>(headers);
//        System.out.println(entity);
//        // Send user data to user-service
//
//        UUID classUuid;
//        UUID levelUuid;
//        UUID termUUid;
//
//        try {
//            classUuid = UUID.fromString(classId);
//            levelUuid = UUID.fromString(levelId);
//            termUUid = UUID.fromString(termId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//        ResponseEntity<Boolean> department_exists = restTemplate.exchange(
//                apiGatewayUrl + "/api/v1/schools/"+existingStudent.getSchoolId()+"/level/"+levelId+"/class/"+classId+"/exists",
//                HttpMethod.GET,
//                entity,
//                boolean.class
//        );
//        System.out.println(department_exists);
//        if(Objects.equals(department_exists.getBody(), false)){
//            throw new ClassNotFoundException("Class does not exist");
//        }
//
//        ResponseEntity<Boolean> term_exists = restTemplate.exchange(
//                apiGatewayUrl + "/api/v1/schools/"+existingStudent.getSchoolId()+"/term/"+termId+"/exists",
//                HttpMethod.GET,
//                entity,
//                boolean.class
//        );
//        // System.out.println(department_exists);
//        if(Objects.equals(term_exists.getBody(), false)){
//            throw new TermNotFoundException("Term provided does not exist");
//        }
//
//        existingStudent.setClassId(classUuid);
//        existingStudent.setLevelId(levelUuid);
//        existingStudent.setYear(year);
//        existingStudent.setTermId(termUUid);
//
//        studentRepository.save(existingStudent);
//        return "Student assigned to class successfully";
//    }
//
//
//    public Boolean parentExistsByEmail(String parentEmail){
//        return studentRepository.existsByParentEmail(parentEmail);
//    }
//
//    @Override
//    public Boolean parentExistsByPhoneAndUUID(String parentPhone, String regParent){
//        UUID regParentUUID;
//        try {
//            regParentUUID = UUID.fromString(regParent);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//        return studentRepository.existsByParentPhoneAndRegParent(parentPhone, regParentUUID);
//    }
//
//    private List<SubjectClassResponse> mapToSubjectClassResponse(List<SubjectClass> sjb){
//
//      return sjb.stream().map( sb ->
//                SubjectClassResponse.builder()
//                        .classId(sb.getClassId())
//                        .subjectId(sb.getSubjectId())
//                        .levelId(sb.getLevelId())
//                        .termId(sb.getTermId())
//                        .year(sb.getYear())
//                        .exams(sb.getExams())
//                        .build()
//        ).collect(Collectors.toList());
//    }
//
//
//    @Override
//    public int countStudentsInClass(Long schoolId, String classId, String levelId, String termId, Year year, String authorizationValue) {
//
//        HttpHeaders headers = new HttpHeaders();
//
//
//        headers.setBearerAuth(authorizationValue);
//        HttpEntity<Object> entity = new HttpEntity<>(headers);
//        System.out.println(entity);
//        // Send user data to user-service
//
//        UUID classUuid;
//        UUID levelUuid;
//        UUID termUUid;
//
//        try {
//            classUuid = UUID.fromString(classId);
//            levelUuid = UUID.fromString(levelId);
//            termUUid = UUID.fromString(termId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//        ResponseEntity<Boolean> department_exists = restTemplate.exchange(
//                apiGatewayUrl + "/api/v1/schools/"+schoolId+"/level/"+levelId+"/class/"+classId+"/exists",
//                HttpMethod.GET,
//                entity,
//                boolean.class
//        );
//        System.out.println(department_exists);
//        if(Objects.equals(department_exists.getBody(), false)){
//            throw new ClassNotFoundException("Class does not exist");
//        }
//
//        ResponseEntity<Boolean> term_exists = restTemplate.exchange(
//                apiGatewayUrl + "/api/v1/schools/"+schoolId+"/term/"+termId+"/exists",
//                HttpMethod.GET,
//                entity,
//                boolean.class
//        );
//        // System.out.println(department_exists);
//        if(Objects.equals(term_exists.getBody(), false)){
//            throw new TermNotFoundException("Term provided does not exist");
//        }
//
//        return this.getStudentBySchoolId(schoolId)
//                .stream().filter(stud ->
//                        classUuid.equals(stud.getClassId())
//                        && levelUuid.equals(stud.getLevelId())
//                        && termUUid.equals(stud.getTermId())
//                        && year.equals(stud.getYear())
//                        )
//                .toList().size();
//    }
//
//    private UserResponse fetchUserById(Long userId) {
//        String userEndpoint = userServiceUrl + "/api/v1/auth/user/id/" + userId;
//        ResponseEntity<UserResponse> userResponse = restTemplate.getForEntity(userEndpoint, UserResponse.class);
//        return userResponse.getBody();
//    }
//
//
//    public StudentResponse updateStudent(Long studentId, StudentUpdateRequest studentUpdateRequest) {
//        // Fetch the existing student
//        Student existingStudent =studentRepository.findById(studentId)
//                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));
//
//        // Update student-specific fields
//        existingStudent.setPhonenumber(studentUpdateRequest.getPhonenumber());
//        existingStudent.setCountry(studentUpdateRequest.getCountry());
//
//        existingStudent.setLevelId(studentUpdateRequest.getLevelId());
//        existingStudent.setTermId(studentUpdateRequest.getTermId());
//
//        existingStudent.setStatus(studentUpdateRequest.getStatus());
//
//        // Save the updated student record
//        Student updatedStudent = studentRepository.save(existingStudent);
//
//        // Update the corresponding user record
//        updateUserDetails(existingStudent.getUserId(), studentUpdateRequest);
//
//        return mapToStudentResponse(updatedStudent);
//    }
//
//    private void updateUserDetails(Long userId, StudentUpdateRequest studentUpdateRequest) {
//        // Construct the API Gateway endpoint for user update
//        String userEndpoint = apiGatewayUrl + "/api/v1/auth/update/id/" + userId;
//
//        // Create a request body for user update
//        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
//        userUpdateRequest.setFirstname(studentUpdateRequest.getFirstname());
//        userUpdateRequest.setLastname(studentUpdateRequest.getLastname());
//        userUpdateRequest.setEmail(studentUpdateRequest.getEmail());
//        userUpdateRequest.setPassword(studentUpdateRequest.getPassword());
//        userUpdateRequest.setRole(studentUpdateRequest.getRole());
//
//        // Set the headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create the HTTP entity with the request body and headers
//        HttpEntity<UserUpdateRequest> entity = new HttpEntity<>(userUpdateRequest, headers);
//
//        // Make the PUT request to update the user via the API Gateway
//        restTemplate.put(userEndpoint, entity);
//    }
//
//
//    public void uploadCsv(MultipartFile file) {
//        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
//            CsvToBean<StudentCsv> csvToBean = new CsvToBeanBuilder<StudentCsv>(csvReader)
//                    .withType(StudentCsv.class)
//                    .withIgnoreLeadingWhiteSpace(true)
//                    .build();
//
//            List<StudentCsv> studentsCsv = csvToBean.parse();
//            for (StudentCsv studentCsv : studentsCsv) {
//                // Create user in auth microservice
//                UserResponse userResponse = createUserFromCsv(studentCsv);
//
//                // Map CSV data to Student entity
//                Student student = new Student();
//                student.setUserId(userResponse.getId()); // Link to the user
//                student.setDob(studentCsv.getDob());
//                student.setPhonenumber(studentCsv.getPhonenumber());
//                student.setCountry(studentCsv.getCountry());
//                student.setBloodGroup(studentCsv.getBloodGroup());
//
//                student.setStatus(studentCsv.getStatus());
//
//                // Save the teacher details
//                studentRepository.save(student);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to upload CSV file: " + e.getMessage());
//        }
//    }
//
//    private UserResponse createUserFromCsv(StudentCsv studentCsv) {
//        // Construct the API Gateway endpoint for user creation
//        String userEndpoint = apiGatewayUrl + "/api/v1/auth/register?createdByAdmin=true";
//
//        // Create a request body for user creation
//        UserRequest userRequest = new UserRequest(
//                studentCsv.getFirstName(),
//                studentCsv.getLastName(),
//                studentCsv.getEmail(),
//                studentCsv.getPassword(), // Ensure proper handling
//                "STUDENT" // Set appropriate role
//        );
//
//        // Set the headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        // Create the HTTP entity with the request body and headers
//        HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, headers);
//
//        // Make the POST request to create the user via the API Gateway
//        ResponseEntity<UserResponse> response = restTemplate.postForEntity(userEndpoint, entity, UserResponse.class);
//
//        return response.getBody(); // Return the created user response
//    }
//
//
//
//    public List<StudentResponse> findStudents(String firstName, String lastName, String email, Status status) {
//        // Fetch all students
//        List<Student> students = studentRepository.findAll();
//
//        // Filter students based on user attributes and student status
//        List<StudentResponse> filteredStudents = students.stream()
//                .filter(student -> {
//                    UserResponse user = fetchUserById(student.getUserId());
//
//                    boolean matches = true;
//                    if (firstName != null && !firstName.isEmpty()) {
//                        matches &= user.getFirstname().toLowerCase().contains(firstName.toLowerCase());
//                    }
//                    if (lastName != null && !lastName.isEmpty()) {
//                        matches &= user.getLastname().toLowerCase().contains(lastName.toLowerCase());
//                    }
//                    if (email != null && !email.isEmpty()) {
//                        matches &= user.getEmail().toLowerCase().contains(email.toLowerCase());
//                    }
//                    if (status != null) {
//                        matches &= student.getStatus().equals(status);
//                    }
//                    return matches;
//                })
//                .map(this::mapToStudentResponse) // Map to StudentResponse
//                .collect(Collectors.toList());
//
//        return filteredStudents; // Return the filtered list
//    }
//
//    @Override
//    public StudentResponse findById(Long id){
//        System.out.println("keep trying");
//        Student student = studentRepository.findById(id)
//                .orElseThrow(() -> new StudentNotFoundException("Student with id:: "+ 1d+" not found."));
//        System.out.println("reached here");
//
//        StudentResponse res=  mapToStudentResponse(student);
//
//        System.out.println(res);
//
//        return res;
//    }
//    @Override
//    public List<StudentResponse> findStudentsByParentEmail(String parentEmail) {
//        List<Student> students = studentRepository.findStudentsByParentEmail(parentEmail);
//
//        return students.stream().map(
//                 this::mapToStudentResponse
//         ).toList();
//    }
//
//    @Override
//    public List<StudentResponse> findStudentsByParentPhone(String parentPhone, String regParent) {
//
//        UUID regParentUUID;
//        try {
//            regParentUUID = UUID.fromString(regParent);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//        List<Student> students = studentRepository.findStudentsByParentPhoneAndRegParent(parentPhone, regParentUUID);
//
//        return students.stream().map(
//                this::mapToStudentResponse
//        ).toList();
//    }
//
//    @Override
//    public List<StudentResponse> getAllStudentsByParentEmail(String parentEmail) {
//
//        List<Student> students = studentRepository.findStudentsByParentEmail(parentEmail);
//        return students.stream().map(this::mapToStudentResponse).toList();
//    }
//
//    @Override
//    public List<StudentResponse> getAllStudentsByParentPhone(String parentPhone) {
//
//        List<Student> students = studentRepository.findStudentsByParentPhone(parentPhone);
//        return students.stream().map(this::mapToStudentResponse).toList();
//    }
//
//    @Transactional
//    @Override
//    public List<SubjectClass> assignSubject(Long studentId, SubjectClassRequest subjectClassRequest, String authorizationValue) {
//        Student existingStudent = studentRepository.findById(studentId)
//                .orElseThrow(() -> new StudentNotFoundException("Student with id:: "+studentId+" not found"));
//
//        HttpHeaders headers = new HttpHeaders();
//
//        SchoolSubClassRequest request = new SchoolSubClassRequest(
//                existingStudent.getSchoolId(),
//                subjectClassRequest.getClassId(),
//                subjectClassRequest.getSubjectId(),
//                subjectClassRequest.getLevelId(),
//                subjectClassRequest.getTermId(),
//                subjectClassRequest.getYear()
//        );
//
//
//        headers.setBearerAuth(authorizationValue);
//        HttpEntity<SchoolSubClassRequest> entity = new HttpEntity<>(request, headers);
//
//        ResponseEntity<Boolean> sub_class_exists = restTemplate.exchange(
//                apiGatewayUrl + "/api/v1/schools/sub_class_exists",
//                HttpMethod.POST,
//                entity,
//                Boolean.class
//        );
//
//        System.out.println("subclass:: "+ sub_class_exists);
//        if (Boolean.FALSE.equals(sub_class_exists.getBody())) {
//            throw new EntityNotFoundException("Subject or class does not exist for the particular level, term and year");
//        }
//
//        // Check for existing combination of subjectId and classId
//        boolean exists = existingStudent.getSubjectClasses().stream()
//                .anyMatch(subject ->
//                                subjectClassRequest.getSubjectId().equals(subject.getSubjectId())
//                        && subjectClassRequest.getClassId().equals(subject.getClassId())
//                        && subjectClassRequest.getLevelId().equals(subject.getLevelId())
//                        && subjectClassRequest.getTermId().equals(subject.getTermId())
//                        && subjectClassRequest.getYear().equals(subject.getYear())
//                );
//
//        System.out.println("exists:: "+ exists);
//        if (exists) {
//            throw new StudentAlreadyAssignedException("Student has already been assigned to the subject in this class for the particulat term, level and yaer");
//        }
//
//        SubjectClass assigned = subjectClassRepository.findByClassIdAndSubjectIdAndLevelIdAndTermIdAndYear(
//                        subjectClassRequest.getClassId(),
//                        subjectClassRequest.getSubjectId(),
//                subjectClassRequest.getLevelId(),
//                subjectClassRequest.getTermId(),
//                subjectClassRequest.getYear()
//                )
//                .orElseGet(() -> {
//                    SubjectClass newSubjectClass = new SubjectClass();
//                    newSubjectClass.setSubjectId(subjectClassRequest.getSubjectId());
//                    newSubjectClass.setClassId(subjectClassRequest.getClassId());
//                    newSubjectClass.setStudents(new ArrayList<>());
//                    newSubjectClass.setLevelId(subjectClassRequest.getLevelId());
//                    newSubjectClass.setTermId(subjectClassRequest.getTermId());
//                    newSubjectClass.setYear(subjectClassRequest.getYear());
//                    return subjectClassRepository.save(newSubjectClass);
//                });
//
//// Add the student to the subject class
//        existingStudent.getSubjectClasses().add(assigned);
//
//        studentRepository.save(existingStudent);
//
//
//        HttpHeaders header = new HttpHeaders();
//
//
//        header.setBearerAuth(authorizationValue);
//        HttpEntity<Object> token = new HttpEntity<>(header);
//
//        ResponseEntity<List<ExamResponse>> exams = restTemplate.exchange(
//                apiGatewayUrl + "/api/v1/teachers/get/future/exams/class/"+subjectClassRequest.getClassId()
//                        +"/subject/"+subjectClassRequest.getSubjectId()
//                        +"/level/"+subjectClassRequest.getLevelId()
//                        +"/term/"+subjectClassRequest.getTermId()
//                        +"/year/"+subjectClassRequest.getYear()
//                ,
//                HttpMethod.GET,
//               token,
//                new ParameterizedTypeReference<List<ExamResponse>>() {}
//        );
//
//        System.out.println("exams:: "+ exams);
//
//        if(!Objects.requireNonNull(exams.getBody()).isEmpty()){
//
//
//            Optional<SubjectClass> sb =  existingStudent.getSubjectClasses().stream()
//                    .filter(ts -> subjectClassRequest.getSubjectId().equals(ts.getSubjectId())
//                            && subjectClassRequest.getClassId().equals(ts.getClassId())
//                            && subjectClassRequest.getLevelId().equals(ts.getLevelId())
//                            && subjectClassRequest.getTermId().equals(ts.getTermId())
//                            && subjectClassRequest.getYear().equals(ts.getYear())
//                    ).findFirst();
//
//            System.out.println(sb.isPresent());
//          //  System.out.println(sb);
//            if(sb.isPresent()){
//                SubjectClass subjectClass = sb.get();
//
//                // Initialize exams list if it's null
//                if (subjectClass.getExams() == null) {
//                    subjectClass.setExams(new ArrayList<>());
//                }
//
//                List<Exam> es = subjectClass.getExams();
//                exams.getBody().forEach(e -> {
//                    Exam ex = new Exam();
//                    ex.setExamName(e.getExamName());
//                    ex.setExamId(e.getExamId());
//                    ex.setExamDate(e.getExamDate());
//                    ex.setPaperNo(e.getPaperNo());
//                    ex.setOutOf(e.getOutOf());
//                    ex.setWeight(e.getWeight());
//                    es.add(ex);
//                });
//            }
//         //   System.out.println("hhhh::: "+ existingStudent);
//          Student s = studentRepository.save(existingStudent);
//
//          //  System.out.println(s);
//        }
//
//       // System.out.println(existingStudent.getSubjectClasses());
//
//      //System.out.println(existingStudent);
//       // Hibernate.initialize(existingStudent.getSubjectClasses());
//
//        return existingStudent.getSubjectClasses();
//    }
//
//    @Transactional
//    @Override
//    public Exam markExamForStudent(
//            Long studentId,
//            String subjectId,
//            String classId,
//            String levelId,
//            String termId,
//            Year year,
//            String examId,
//            Double mark,
//            String authValue
//    ){
//
//        Student existingStudent = studentRepository.findById(studentId)
//                .orElseThrow(() -> new StudentNotFoundException("Student with id:: "+studentId+" not found"));
//
//        UUID classuuid;
//        UUID subjectUuid;
//        UUID examUuid;
//        UUID levelUuid;
//        UUID termUuid;
//
//        try {
//            classuuid = UUID.fromString(classId);
//            subjectUuid = UUID.fromString(subjectId);
//            examUuid = UUID.fromString(examId);
//            levelUuid = UUID.fromString(levelId);
//            termUuid = UUID.fromString(termId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//        System.out.println("stud....");
//        Optional<SubjectClass> subjectClas = subjectClassRepository.findById(10L);
//      //  subjectClas.ifPresent(sc -> System.out.println(sc));
//
//        boolean exists = existingStudent.getSubjectClasses().stream()
//                .anyMatch(subject -> subject.getSubjectId().equals(subjectUuid)
//                                && subject.getClassId().equals(classuuid)
//                        && subject.getLevelId().equals(levelUuid)
//                        && subject.getTermId().equals(termUuid)
//                        && subject.getYear().equals(year)
//                );
//
//        System.out.println("exists:: "+ exists);
//
//        Optional<SubjectClass> sb =  existingStudent.getSubjectClasses().stream()
//                .filter(ts -> ts.getSubjectId().equals(subjectUuid)
//                        && ts.getClassId().equals(classuuid)
//                        && ts.getLevelId().equals(levelUuid)
//                        && ts.getTermId().equals(termUuid)
//                        && ts.getYear().equals(year)
//                ).findFirst();
//
////        System.out.println(mapToStudentResponse(existingStudent));
////        System.out.println(mapToSubjectClassResponse(existingStudent.getSubjectClasses()));
////        System.out.println(sb.isPresent());
//        if(sb.isPresent()){
//            SubjectClass subjectClass = sb.get();
//
//            // Initialize exams list if it's null
//            if (subjectClass.getExams() == null) {
//                throw new NoExamException("No exam present in the subject and class specified in the term, level and year specified");
//            }
//
//            List<Exam> es = subjectClass.getExams();
//
//           Optional<Exam> exam = es.stream().filter(s -> examUuid.equals(s.getExamId())).findFirst();
//            if(exam.isPresent()){
//                if(mark > exam.get().getOutOf()){
//                    throw new MarkException("Acquired mark can not be greater than the exam total mark ");
//                }
//                exam.get().setExamMark(mark);
//            }
//
//          Double finalMark =  subjectClass.calculateFinalMark();
//            String grade = fetchGradingScale(existingStudent.getSchoolId(),subjectClass.getLevelId(),finalMark, authValue);
//
//            subjectClass.assignGrade(grade);
//            studentRepository.save(existingStudent);
//            return exam.get();
//        }
//
//        return null;
//    }
//
//        private String fetchGradingScale(Long schoolId, UUID levelId, Double score, String authorizationValue) {
//
//            HttpHeaders header = new HttpHeaders();
//
//
//            header.setBearerAuth(authorizationValue);
//            HttpEntity<Object> token = new HttpEntity<>(header);
//
//            String url = apiGatewayUrl + "/api/v1/schools/" + schoolId+ "/level/"+ levelId + "/score/"+score+"/grading-scale";
//            // @GetMapping("/{schoolId}/level/{levelId}/score/{score}/grading-scale")
//            //
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    token,
//                    new ParameterizedTypeReference<String>() {}
//            );
//            System.out.println(response);
//            return response.getBody();
//        }
//
//    @Override
//    public List<Student> getStudentBySchoolId(Long schoolId){
//        List<Student> students = studentRepository.findAll();
//
//        return students.stream()
//                .filter(student -> schoolId.equals(student.getSchoolId()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<StudentResponse> getAllStudentsInClass(Long schoolId, String levelId, String classId, String termId, Year year){
//
//        List<Student> students = studentRepository.findAll();
//
//        UUID levelUuid;
//        UUID classuuid;
//        UUID termUuid;
//        try {
//            levelUuid = UUID.fromString(levelId);
//            classuuid = UUID.fromString(classId);
//            termUuid = UUID.fromString(termId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//        List<Student> s = students.stream()
//                .filter(student -> schoolId.equals(student.getSchoolId())
//                                    && levelUuid.equals(student.getLevelId())
//                                    && classuuid.equals(student.getClassId())
//                                    && termUuid.equals(student.getTermId())
//                                    && year.equals(student.getYear())
//                )
//                .toList();
//
//        return s.stream().map(this::mapToStudentResponse).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<StudentResponse> getAllStudentsInSubjectClass(Long schoolId, String levelId, String classId, String termId, Year year, String subjectId){
//
//        List<Student> students = studentRepository.findAll();
//
//        UUID levelUuid;
//        UUID classuuid;
//        UUID termUuid;
//        UUID subjectUuid;
//        try {
//            levelUuid = UUID.fromString(levelId);
//            classuuid = UUID.fromString(classId);
//            termUuid = UUID.fromString(termId);
//            subjectUuid = UUID.fromString(subjectId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//        List<Student> studentsInSubjectClass = subjectClassRepository.findAll()
//                .stream().filter(
//                        sbc -> levelUuid.equals(sbc.getLevelId())
//                                && subjectUuid.equals(sbc.getSubjectId())
//                                && classuuid.equals(sbc.getClassId())
//                                && termUuid.equals(sbc.getTermId())
//                                && year.equals(sbc.getYear())
//                ) .flatMap(sbc -> sbc.getStudents().stream())
//                .toList()
//                .stream().filter(s -> schoolId.equals(s.getSchoolId())).toList()
//                ;
//
//        return studentsInSubjectClass.stream().map(this::mapToStudentResponse).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Student> getStudentBySchoolIdAndClassId(Long schoolId, String classId){
//        List<Student> students = studentRepository.findAll();
//
//
//      //  System.out.println("hhhhh");
//        return students.stream()
//                .filter(student ->
//                        schoolId.equals(student.getSchoolId())
//                                             )
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<Student> getStudentWithSubjectsClass(Long studentId) {
//        return studentRepository.findStudentWithSubjectsCLass(studentId);
//    }
//
//    @Transactional
//    @Override
//    public String assignExamToClass(Long schoolId,
//                                    String classId,
//                                    String subjectId,
//                                    ExamAssignRequest examAssignRequest
//
//    ) {
//       // System.out.println("trtrtrtr");
//        List<Student> schoolClassStudents = getStudentBySchoolId(schoolId);
//
//      //  System.out.println("eshiri");
//        UUID classuuid;
//        UUID subjectuuid;
//
//        try {
//            classuuid = UUID.fromString(classId);
//            subjectuuid = UUID.fromString(subjectId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
////        System.out.println(classuuid);
////        System.out.println(subjectuuid);
//        schoolClassStudents.stream()
//                .filter(
//                existingStudent ->  existingStudent.getSubjectClasses().stream()
//                        .anyMatch(ts ->
//                                subjectuuid.equals(ts.getSubjectId())
//                                        && classuuid.equals(ts.getClassId())
//                                        && examAssignRequest.getLevelId().equals(ts.getLevelId())
//                                        && examAssignRequest.getTermId().equals(ts.getTermId())
//                                        && examAssignRequest.getYear().equals(ts.getYear())
//                        )).forEach(
//                        student ->{
//                            System.out.println("hello");
//                            Exam exam = new Exam();
//                           exam.setExamDate(examAssignRequest.getExamDate());
//                           exam.setExamName(examAssignRequest.getExamName());
//                           exam.setExamId(examAssignRequest.getExamId());
//                           exam.setWeight(examAssignRequest.getWeight());
//                           exam.setPaperNo(examAssignRequest.getPaperNo());
//                           exam.setOutOf(examAssignRequest.getOutOf());
//
//                         student.getSubjectClasses().forEach(
//                                    subjectClass -> subjectClass.getExams().add(exam)
//
//                            );
//
//                           studentRepository.save(student);
//                        }
//                )
//        ;
//
//
//        return "Exam assigned to the students in the class specified for the level, term and year specified";
//    }
//
//    @Transactional
//    @Override
//    public String assignExamToClasses(Long schoolId,
//                                    List<String> classIds,
//                                    String subjectId,
//                                    ExamAssignRequest examAssignRequest
//
//    ) {
//        // System.out.println("trtrtrtr");
//        List<Student> schoolClassStudents = getStudentBySchoolId(schoolId);
//
//        //  System.out.println("eshiri");
//        List<UUID> classuuids = new ArrayList<>();
//        UUID subjectuuid;
//
//        classIds.forEach(id -> {
//            try {
//                UUID ci = UUID.fromString(id);
//                classuuids.add(ci);
//            } catch (IllegalArgumentException e) {
//                throw new InvalidUUIDFormat("Invalid UUID format");
//            }
//        });
//
//        try {
//
//            subjectuuid = UUID.fromString(subjectId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//
//
// classuuids.forEach(id -> {
//     schoolClassStudents.stream()
//             .filter(
//                     existingStudent ->  existingStudent.getSubjectClasses().stream()
//                             .anyMatch(ts ->
//                                     subjectuuid.equals(ts.getSubjectId())
//                                             && id.equals(ts.getClassId())
//                                             && examAssignRequest.getLevelId().equals(ts.getLevelId())
//                                             && examAssignRequest.getTermId().equals(ts.getTermId())
//                                             && examAssignRequest.getYear().equals(ts.getYear())
//                             )).forEach(
//                     student ->{
//                         System.out.println("hello");
//                         Exam exam = new Exam();
//                         exam.setExamDate(examAssignRequest.getExamDate());
//                         exam.setExamName(examAssignRequest.getExamName());
//                         exam.setExamId(examAssignRequest.getExamId());
//                         exam.setWeight(examAssignRequest.getWeight());
//                         exam.setPaperNo(examAssignRequest.getPaperNo());
//                         exam.setOutOf(examAssignRequest.getOutOf());
//
//                         student.getSubjectClasses().forEach(
//                                 subjectClass -> subjectClass.getExams().add(exam)
//
//                         );
//
//                         studentRepository.save(student);
//                     }
//             )
//     ;
// });
//
//
//
//        return "Exam assigned to the students in the class specified for the level, term and year specified";
//    }
//
//    @Transactional
//    @Override
//    public String putCommentToStudent(Long schoolId,
//                                      Long studentId,
//                                      String classId,
//                                      String subjectId,
//                                      String termId,
//                                      String levelId,
//                                      Year year,
//                                      String comment
//
//    ) {
//        // System.out.println("trtrtrtr");
//
//        Student student = getStudentBySchoolId(schoolId).stream().filter(stud -> studentId.equals(stud.getId()))
//                .findFirst().orElseThrow(() -> new StudentNotFoundException("Student with id:: "+ studentId + " not found"));
//
//        //  System.out.println("eshiri");
//        UUID classuuid;
//        UUID subjectuuid;
//        UUID termUuid;
//        UUID levelUuid;
//
//        try {
//            classuuid = UUID.fromString(classId);
//            subjectuuid = UUID.fromString(subjectId);
//            termUuid = UUID.fromString(termId);
//            levelUuid = UUID.fromString(levelId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
////        System.out.println(classuuid);
////        System.out.println(subjectuuid);
//        SubjectClass sb = student.
//                getSubjectClasses()
//                .stream()
//                .filter(ts ->
//                        subjectuuid.equals(ts.getSubjectId())
//                                && classuuid.equals(ts.getClassId())
//                                && levelUuid.equals(ts.getLevelId())
//                                && termUuid.equals(ts.getTermId())
//                                && year.equals(ts.getYear())
//                ).findFirst().orElseThrow(() -> new NoExamException("Exam not found"));
//
//        sb.setComment(comment);
//
//        studentRepository.save(student);
//
//
//        return "Comment put to the students in the class specified for the level, term and year specified";
//    }
//
//
//    @Transactional
//    @Override
//    public ReportCardResponse generateReportCardForStudent(Long schoolId,
//                                                           Long studentId,
//                                                           String classId,
//                                                           String termId,
//                                                           String levelId,
//                                                           Year year
//
//    ) {
//        // System.out.println("trtrtrtr");
//
//
//        //  System.out.println("eshiri");
//        UUID classuuid;
//        UUID termUuid;
//        UUID levelUuid;
//
//        try {
//            classuuid = UUID.fromString(classId);
//            termUuid = UUID.fromString(termId);
//            levelUuid = UUID.fromString(levelId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDFormat("Invalid UUID format");
//        }
//
//        Student student = getStudentBySchoolId(schoolId).stream().filter(stud -> studentId.equals(stud.getId())
//                        &&  classuuid.equals(stud.getClassId())
//                        && levelUuid.equals(stud.getLevelId())
//                        && termUuid.equals(stud.getTermId())
//                        && year.equals(stud.getYear())
//                )
//                .findFirst().orElseThrow(() -> new StudentNotFoundException("Student not found"));
//
//        List<ReportSubResponse> subs = new ArrayList<>();
//         student.
//                getSubjectClasses()
//                .forEach(s -> {
//                    ReportSubResponse rps = ReportSubResponse.builder()
//                            .finalMark(s.getFinalMark())
//                            .grade(s.getGrade())
//                            .comment(s.getComment())
//                            .subjectId(s.getSubjectId())
//                            .build();
//
//                    subs.add(rps);
//                });
//
//
//
//
//        return ReportCardResponse.builder()
//                .id(student.getId())
//                .userId(student.getUserId())
//                .firstName(student.getFirstName())
//                .lastName(student.getLastName())
//                .middleName(student.getMiddleName())
//                .email(student.getEmail())
//                .classId(classuuid)
//                .levelId(levelUuid)
//                .termId(termUuid)
//                .schoolId(student.getSchoolId())
//                .year(year)
//                .subs(subs)
//                .build();
//    }
//
//
//    public String uploadExcelFile(MultipartFile file, String authorizationValue) throws IOException {
//        if (file.isEmpty()) {
//            return "Please upload a file.";
//        }
//
//        System.out.println("Previous authorization " + authorizationValue);
//
//        List<StudentRequest> studentRequests = parseExcelFile(file);
//        for (StudentRequest studentRequest : studentRequests) {
//            // Save each student and get the BSNumber
//            StudentWithBsNumber result = saveStudent(studentRequest, null, authorizationValue); // Pass null for picture
//
//            // Send SMS to the parent with the generated BSNumber
////            String senderId = studentRequest.getSender_id(); // Get sender_id from the request
////            smsService.sendSms(senderId, result.getStudent().getParentPhone(),
////                    "Your child's BSNumber is: " + result.getBsNumber());
//        }
//
//        return "File uploaded and students created successfully.";
//    }
//
//    private List<StudentRequest> parseExcelFile(MultipartFile file) {
//        List<StudentRequest> studentRequests = new ArrayList<>();
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = WorkbookFactory.create(inputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // Skip header row
//
//                StudentRequest studentRequest = new StudentRequest();
//                studentRequest.setFirstName(ExcelUtils.getStringCellValue(row.getCell(0))); // First Name
//                studentRequest.setLastName(ExcelUtils.getStringCellValue(row.getCell(1))); // Last Name
//                studentRequest.setEmail(ExcelUtils.getStringCellValue(row.getCell(2))); // Email
//                studentRequest.setPassword(ExcelUtils.getStringCellValue(row.getCell(3))); // Password
//
//                // Date of Birth
//                Date dob = ExcelUtils.getDateCellValue(row.getCell(4)); // Date of Birth
//                studentRequest.setDob(dob); // Set the Date of Birth
//
//                studentRequest.setPhonenumber(ExcelUtils.getStringCellValue(row.getCell(5))); // Phone Number
//                studentRequest.setAddress(ExcelUtils.getStringCellValue(row.getCell(6))); // Address
//                studentRequest.setDistrict(ExcelUtils.getStringCellValue(row.getCell(7))); // District
//                studentRequest.setProvince(ExcelUtils.getStringCellValue(row.getCell(8))); // Province
//                studentRequest.setCountry(ExcelUtils.getStringCellValue(row.getCell(9))); // Country
//                studentRequest.setBloodGroup(ExcelUtils.getStringCellValue(row.getCell(10))); // Blood Group
//                studentRequest.setParentEmail(ExcelUtils.getStringCellValue(row.getCell(11))); // Parent Email
//                studentRequest.setParentPhone(ExcelUtils.getStringCellValue(row.getCell(12))); // Parent Phone
//                studentRequest.setSender_id(ExcelUtils.getStringCellValue(row.getCell(13))); // Sender ID
//                studentRequest.setStatus(Status.valueOf(ExcelUtils.getStringCellValue(row.getCell(14)))); // Status
//
//                studentRequests.add(studentRequest);
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // Handle exceptions properly (e.g., logging)
//        }
//
//        return studentRequests;
//    }
//
//
//    // Helper method to safely get string values
//    private String getStringCellValue(Cell cell) {
//        if (cell == null) {
//            return ""; // Return an empty string or a default value
//        }
//        switch (cell.getCellType()) {
//            case STRING:
//                return cell.getStringCellValue();
//            case NUMERIC:
//                // If the cell is numeric, convert it to string
//                return String.valueOf((long) cell.getNumericCellValue()); // Cast to long to avoid decimal issues
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                // Handle formula cells if necessary
//                return cell.getCellFormula(); // Or evaluate the formula
//            default:
//                return ""; // Return an empty string for other types
//        }
//    }
//
//    @Override
//    public List<StudentResponse> getAllStudentsBySchool(Long schoolId) {
//        // System.out.println("trying here");
//        List<Student> students = studentRepository.findAll();
//
//        return students.stream()
//                .filter(student -> schoolId.equals(student.getSchoolId()))
//                .map(this::mapToStudentResponse)
//                .sorted(Comparator.comparing(StudentResponse::getId).reversed())
//                .collect(Collectors.toList());
//    }
//
//
//
//
//
//
//
//
//
//    // Method to get levelId from school service
//    private UUID getLevelIdFromSchoolService(Long schoolId, String level, String authorizationValue) {
//
//
//
//        // Set the headers
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.setBearerAuth(authorizationValue);
//        // Create the HTTP entity with the request body and headers
//        HttpEntity<Object> entity = new HttpEntity<>(headers);
//
//
//
//        String url = apiGatewayUrl + "/api/v1/schools/getLevelId?schoolId=" + schoolId + "&level=" + URLEncoder.encode(level, StandardCharsets.UTF_8);
//
//        System.out.println("ESHIRI " + url);
//
//        ResponseEntity<UUID> response = restTemplate.exchange(url, HttpMethod.GET, entity, UUID.class);
//
//        System.out.println("sdfghjkl;lkjhfghjkl " + response);
//
//
//        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//            return response.getBody();
//        }
//        return null; // Return null if level ID is not found
//    }
//
//    // Method to get classId from school service
//    private UUID getClassIdFromSchoolService(Long schoolId, String className, UUID levelId, String authorizationValue) {
//
//
//        // Set the headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(authorizationValue);
//        // Create the HTTP entity with the request body and headers
//        HttpEntity<Object> entity = new HttpEntity<>(headers);
//
//
//        String url = apiGatewayUrl + "/api/v1/schools/getClassId?schoolId=" + schoolId + "&className=" + URLEncoder.encode(className, StandardCharsets.UTF_8) + "&levelId=" + levelId;
//       // ResponseEntity<UUID> response = restTemplate.getForEntity(url, UUID.class);
//
//        ResponseEntity<UUID> response = restTemplate.exchange(url, HttpMethod.GET, entity, UUID.class);
//
//        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//            return response.getBody();
//        }
//        return null; // Return null if class ID is not found
//    }
//
//
//
//    public String uploadExcelFileClass(MultipartFile file, String authorizationValue) throws IOException {
//        if (file.isEmpty()) {
//            return "Please upload a file.";
//        }
//
//
//        List<StudentRequest> studentRequests = parseExcelFileClass(file);
//        for (StudentRequest studentRequest : studentRequests) {
//
//            StudentWithBsNumber studentWithBsNumber = saveStudent(studentRequest, null, authorizationValue);
//
//            // Retrieve levelId and classId from the school service
//            UUID levelId = getLevelIdFromSchoolService(studentWithBsNumber.getStudent().getSchoolId(), studentRequest.getLevel(), authorizationValue);
//            UUID classId = getClassIdFromSchoolService(studentWithBsNumber.getStudent().getSchoolId(), studentRequest.getClassName(), levelId, authorizationValue);
//
//            // Check if levelId and classId are valid
//            if (levelId == null) {
//                return "Invalid Level: " + studentRequest.getLevel();
//            }
//            if (classId == null) {
//                return "Invalid Class: " + studentRequest.getClassName();
//            }
//
//            Student student = studentWithBsNumber.getStudent();
//
//            student.setLevelId(levelId);
//            student.setClassId(classId);
//            studentRepository.save(student);
//
//            // Optional: Send SMS to the parent with the generated BSNumber
//            // String senderId = studentRequest.getSender_id();
//            // smsService.sendSms(senderId, result.getStudent().getParentPhone(),
//            //         "Your child's BSNumber is: " + result.getBsNumber());
//        }
//
//        return "File uploaded and students created successfully.";
//    }
//
//    private List<StudentRequest> parseExcelFileClass(MultipartFile file) {
//        List<StudentRequest> studentRequests = new ArrayList<>();
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Workbook workbook = WorkbookFactory.create(inputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // Skip header row
//
//                StudentRequest studentRequest = new StudentRequest();
//                studentRequest.setFirstName(ExcelUtils.getStringCellValue(row.getCell(0))); // First Name
//                studentRequest.setLastName(ExcelUtils.getStringCellValue(row.getCell(1))); // Last Name
//                studentRequest.setEmail(ExcelUtils.getStringCellValue(row.getCell(2))); // Email
//                studentRequest.setPassword(ExcelUtils.getStringCellValue(row.getCell(3))); // Password
//
//                // Date of Birth
//                Date dob = ExcelUtils.getDateCellValue(row.getCell(4)); // Date of Birth
//                studentRequest.setDob(dob); // Set the Date of Birth
//
//                studentRequest.setPhonenumber(ExcelUtils.getStringCellValue(row.getCell(5))); // Phone Number
//                studentRequest.setAddress(ExcelUtils.getStringCellValue(row.getCell(6))); // Address
//                studentRequest.setDistrict(ExcelUtils.getStringCellValue(row.getCell(7))); // District
//                studentRequest.setProvince(ExcelUtils.getStringCellValue(row.getCell(8))); // Province
//                studentRequest.setCountry(ExcelUtils.getStringCellValue(row.getCell(9))); // Country
//                studentRequest.setBloodGroup(ExcelUtils.getStringCellValue(row.getCell(10))); // Blood Group
//                studentRequest.setParentEmail(ExcelUtils.getStringCellValue(row.getCell(11))); // Parent Email
//                studentRequest.setParentPhone(ExcelUtils.getStringCellValue(row.getCell(12))); // Parent Phone
//                studentRequest.setSender_id(ExcelUtils.getStringCellValue(row.getCell(13))); // Sender ID
//                studentRequest.setStatus(Status.valueOf(ExcelUtils.getStringCellValue(row.getCell(14)))); // Status
//
//                // New fields for Level and Class Name
//                studentRequest.setLevel(ExcelUtils.getStringCellValue(row.getCell(15))); // Level
//                studentRequest.setClassName(ExcelUtils.getStringCellValue(row.getCell(16))); // Class Name
//
//                studentRequests.add(studentRequest);
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // Handle exceptions properly (e.g., logging)
//        }
//
//        return studentRequests;
//    }
//
}


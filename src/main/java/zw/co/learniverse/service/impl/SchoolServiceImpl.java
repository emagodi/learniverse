package zw.co.learniverse.service.impl;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.entities.School;
import java.io.File;
import zw.co.learniverse.payload.request.SchoolRequest;
import zw.co.learniverse.repository.SchoolClassRepository;
import zw.co.learniverse.repository.SchoolRepository;
import zw.co.learniverse.service.SchoolService;


import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;




@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final PasswordEncoder passwordEncoder;


    @PersistenceContext
    private EntityManager entityManager;
    @Value("${file.upload.directory}")
    private String uploadDir;

    @Override
    public School createSchool(SchoolRequest schoolRequest) throws IOException, java.io.IOException {


        // Create a new School object
        var school = School.builder()
                .name(schoolRequest.getName())
                .town(schoolRequest.getTown())
                .streetName(schoolRequest.getStreetName())
                .district(schoolRequest.getDistrict())
                .province(schoolRequest.getProvince())
                .region(schoolRequest.getRegion())
                .country(schoolRequest.getCountry())
                .schoolContact(schoolRequest.getSchoolContact())
                .schoolEmail(schoolRequest.getSchoolEmail())
                .headFirstname(schoolRequest.getHeadFirstname())
                .headLastname(schoolRequest.getHeadLastname())
                .headEmail(schoolRequest.getHeadEmail())
                .headPhonenumber(schoolRequest.getHeadPhonenumber())
                .adminFirstname(schoolRequest.getAdminFirstname())
                .adminLastName(schoolRequest.getAdminLastName())
                .adminEmail(schoolRequest.getAdminEmail())
                .adminPhonenumber(schoolRequest.getAdminPhonenumber())
                .numberOfStudents(schoolRequest.getNumberOfStudents())
                .numberOfTeachers(schoolRequest.getNumberOfTeachers())
                .adminNote(schoolRequest.getAdminNote())
                .responsibleAuthority(schoolRequest.getResponsibleAuthority())
                .ownershipType(schoolRequest.getOwnershipType())
                .status(schoolRequest.getStatus())
                .paymentReferenceNumber(schoolRequest.getPaymentReferenceNumber())
                .paymentMethod(schoolRequest.getPaymentMethod())
                .adminPassword(passwordEncoder.encode(schoolRequest.getAdminPassword())) // Hash password
                .build();

        // Handle logo upload
        if (schoolRequest.getLogo() != null && !schoolRequest.getLogo().isEmpty()) {
            MultipartFile logoFile = schoolRequest.getLogo();
            String logoUrl = uploadFileToLocalFolder(logoFile, "logos");
            school.setLogo(logoUrl); // Set the logo URL
        }

        // Handle receipt upload
        if (schoolRequest.getReceipt() != null && !schoolRequest.getReceipt().isEmpty()) {
            MultipartFile receiptFile = schoolRequest.getReceipt();
            String receiptUrl = uploadFileToLocalFolder(receiptFile, "receipts");
            school.setReceipt(receiptUrl); // Set the receipt URL
        }

        // Save the school entity to the repository and return it
        return schoolRepository.save(school);
    }

    private String uploadFileToLocalFolder(MultipartFile file, String folder) throws IOException, java.io.IOException {
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
                throw new IOException("Failed to create directory: " + fullUploadPath);
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

//    @Override
//    @Transactional
//    public School update(Long schoolId, SchoolRequest updatedSchoolRequest, MultipartFile logo, MultipartFile receipt, String authorizationValue) throws IOException, java.io.IOException {
//        // Retrieve the existing school from the repository
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        School sch;
//        // Store the previous status
//        String previousStatus = String.valueOf(existingSchool.getStatus());
//
//        // Copy non-null properties from the updatedSchoolRequest to the existingSchool
//        copyNonNullProperties(updatedSchoolRequest, existingSchool);
//
//        // Handle the logo file if it's provided
//        if (logo != null && !logo.isEmpty()) {
//            String logoUrl = s3Service.uploadFile(logo); // Upload to S3
//            existingSchool.setLogo(logoUrl); // Set the S3 URL to the logo field
//        }
//
//        // Handle the receipt file if it's provided
//        if (receipt != null && !receipt.isEmpty()) {
//            String receiptUrl = s3Service.uploadFile(receipt); // Upload to S3
//            existingSchool.setReceipt(receiptUrl); // Set the S3 URL to the receipt field
//        }
//
//        String pass = "Password@123";
//
//        if (updatedSchoolRequest.getAdminPassword() != null) {
//            if (!updatedSchoolRequest.getAdminPassword().isEmpty() &&
//                    !updatedSchoolRequest.getAdminPassword().isBlank()) {
//                pass = updatedSchoolRequest.getAdminPassword();
//            }
//        }
//
//        String adminFirstname = existingSchool.getAdminFirstname();
//        if (updatedSchoolRequest.getAdminFirstname() != null) {
//            if (!updatedSchoolRequest.getAdminFirstname().isEmpty() &&
//                    !updatedSchoolRequest.getAdminFirstname().isBlank()) {
//                adminFirstname = updatedSchoolRequest.getAdminFirstname();
//            }
//        }
//
//        String adminEmail = existingSchool.getAdminEmail();
//        if (updatedSchoolRequest.getAdminEmail() != null) {
//            if (!updatedSchoolRequest.getAdminEmail().isEmpty() &&
//                    !updatedSchoolRequest.getAdminEmail().isBlank()) {
//                adminEmail = updatedSchoolRequest.getAdminEmail();
//            }
//        }
//
//        String adminLastname = existingSchool.getAdminLastName();
//        if (updatedSchoolRequest.getAdminLastName() != null) {
//            if (!updatedSchoolRequest.getAdminLastName().isEmpty() &&
//                    !updatedSchoolRequest.getAdminLastName().isBlank()) {
//                adminLastname = updatedSchoolRequest.getAdminLastName();
//            }
//        }
//
//        //System.out.println(updatedSchoolRequest);
//
//
//
//        // Check if the status is changing to "ACTIVE"
//       // System.out.println(previousStatus);
//        if ("ACTIVE".equalsIgnoreCase(String.valueOf(updatedSchoolRequest.getStatus())) && !"ACTIVE".equalsIgnoreCase(previousStatus)) {
//            // Create user request for the admin, mapping fields as required
//            if (updatedSchoolRequest.getSender_id() == null || updatedSchoolRequest.getSender_id().isBlank()) {
//                System.out.println(updatedSchoolRequest.getSender_id());
//                throw new SenderIdException("Sender Id cannot be empty or blank");
//            }
//            UserRequest userRequest = new UserRequest(
//                    adminFirstname,  // Maps to firstname
//                    adminLastname,   // Maps to lastname
//                    adminEmail,       // Maps to email
//                    pass,
//                    updatedSchoolRequest.getSender_id(), // Ensure this is securely handled
//                    ADMIN,
//                    schoolId,
//                    true
//            );
//
//            HttpHeaders headers = new HttpHeaders();
//             headers.setBearerAuth(authorizationValue);
//            HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, headers);
//            System.out.println(entity);
//
//            // Send user data to user-service
//            ResponseEntity<UserResponse> userResponse = restTemplate.postForEntity(
//                    apiGatewayUrl + "/api/v1/auth/register?createdByAdmin=true",
//                    entity,
//                    UserResponse.class
//            );
//
//            // Extract user ID from the response
//            String senderId = Objects.requireNonNull(userResponse.getBody()).getSenderId();
//            existingSchool.setSender_id(senderId);
//
//
//           existingSchool = schoolRepository.save(existingSchool);
//
//            Sms sms = Sms.builder()
//                    .senderId("Brainstake")
//                    .phoneNumber(existingSchool.getAdminPhonenumber())
//                    .email(existingSchool.getAdminEmail())
//                    .bsNumber(userResponse.getBody().getBsNumber())
//                    .password("Password@123")
//                    .build();
//            smsRepository.save(sms);
//        }
//
//
//        // Save the updated school back to the repository
//
//
//        return schoolRepository.save(existingSchool);
//    }
//
//
//    @Override
//    public List<SubjectResponse> getAllSubjectFromSchool(Long schoolId){
//
//        School school = schoolRepository.findById(schoolId).orElse(null);
//
//        if(school == null){
//           throw new SchoolNotFoundException("School with id:: "+schoolId+" not found");
//        }
//        var subjects = school.getSubjects();
//        return  subjects.stream().map(subjects1 -> SubjectResponse.builder()
//                .subjectName(subjects1.getSubjectName())
//                .build()).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<LevelGrade> getAllLevelsFromSchool(Long schoolId){
//
//        School school = schoolRepository.findById(schoolId).orElse(null);
//
//        if(school == null){
//            throw new SchoolNotFoundException("School with id:: "+schoolId+" not found");
//        }
//        return  school.getLevelGrade();
//    }
//
//    @Override
//    public LevelGrade getAllLevelsFromSchoolByName(Long schoolId, String levelName){
//
//        School school = schoolRepository.findById(schoolId).orElse(null);
//
//        if(school == null){
//            throw new SchoolNotFoundException("School with id:: "+schoolId+" not found");
//        }
//        return  school.getLevelGrade().stream().filter(e -> levelName.equals(e.getName())).findFirst().orElseThrow(() -> new LevelNotFoundException("Level "+levelName+" not found!!!"));
//    }
//
//
//    public String createSubject(Long schoolId,String className){
//        School school = schoolRepository.findById(schoolId).orElse(null);
//
//        if(school == null){
//            throw new RuntimeException("School not found");
//        }
//return "222";
//    }
//
//    @Override
//    public List<ClassResponse> getAllClassesFromSchool(Long schoolId){
//
//        School school = schoolRepository.findById(schoolId).orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        var classes = school.getSchoolClass();
//        return  classes.stream().map(class1 -> ClassResponse.builder()
//                .classId(class1.getSchoolClassId())
//                .className(class1.getName())
//                .build()).collect(Collectors.toList());
//    }
//
//
//    // Method to generate the registration number
//    public String generateBsNumber(String town, String schoolName) {
//        // Get the current year
//        String year = String.valueOf(Year.now().getValue());
//
//        // Count the number of existing schools with the same town and name
//        Long count = schoolRepository.countByTownAndName(town, schoolName);
//
//        // Create a sequential number (count + 1)
//        String sequentialNumber = String.format("%03d", count + 1); // Format to 3 digits
//
//        // Generate registration number
//        return String.format("%s-%s-%s-%s",
//                town.toUpperCase(),
//                year,
//                schoolName.toUpperCase().replaceAll("\\s+", ""), // Remove spaces in school name
//                sequentialNumber);
//    }
//
//
//
//    public List<SchoolResponse> getAllSchools() {
//        List<School> schools = schoolRepository.findAll();
//        return schools.stream()
//                .map(this::mapToSchoolResponse)
//                .sorted(Comparator.comparing(SchoolResponse::getCreatedAt).reversed())
//                .collect(Collectors.toList());
//    }
//
//
//    private SchoolResponse mapToSchoolResponse(School school) {
//        return SchoolResponse.builder()
//                .id(school.getId())
//                .name(school.getName())
//                .streetName(school.getStreetName())
//                .bsNumber(school.getBsNumber())
//                .town(school.getTown())
//                .district(school.getDistrict())
//                .province(school.getProvince())
//                .country(school.getCountry())
//                .schoolContact(school.getSchoolContact())
//                .schoolEmail(school.getSchoolEmail())
//                .headFirstname(school.getHeadFirstname())
//                .headLastname(school.getHeadLastname())
//                .headEmail(school.getHeadEmail())
//                .headPhonenumber(school.getHeadPhonenumber())
//                .adminFirstname(school.getAdminFirstname())
//                .adminLastName(school.getAdminLastName())
//                .adminEmail(school.getAdminEmail())
//                .adminPhonenumber(school.getAdminPhonenumber())
//                .numberOfStudents(school.getNumberOfStudents())
//                .numberOfTeachers(school.getNumberOfTeachers())
//                .adminNote(school.getAdminNote())
//                .responsibleAuthority(school.getResponsibleAuthority())
//                .ownershipType(school.getOwnershipType())
//                .paymentMethod(school.getPaymentMethod())
//                .logoUrl(school.getLogo())
//                .receiptUrl(school.getReceipt())
//                .status(school.getStatus())
//                .sender_id(school.getSender_id())
//                .departments(school.getDepartments())
//                .schoolClass(school.getSchoolClass())
//                .levelGrades(school.getLevelGrade())
//                .subjects(school.getSubjects())
//                .schoolTerms(school.getSchoolTerms())
////                .academicYears(school.getAcademicYears())
//                .createdAt(school.getCreatedAt())
//                .createdBy(school.getCreatedBy())
//                .updatedAt(school.getUpdatedAt())
//                .updatedBy(school.getUpdatedBy())
//                .build();
//    }
//
////    @Override
////    public School update(School school, MultipartFile receiptFile) throws java.io.IOException {
////        School existingSchool  = schoolRepository.findById(school.getId()).get();
////
////        existingSchool.setName(school.getName());
////        existingSchool.setStreetName(school.getStreetName());
////        existingSchool.setBsNumber(school.getBsNumber());
////        existingSchool.setTown(school.getTown());
////        existingSchool.setDistrict(school.getDistrict());
////        existingSchool.setProvince(school.getProvince());
////        existingSchool.setCountry(school.getCountry());
////        existingSchool.setSchoolContact(school.getSchoolContact());
////        existingSchool.setSchoolEmail(school.getSchoolEmail());
////        existingSchool.setHeadFirstname(school.getHeadFirstname());
////        existingSchool.setHeadLastname(school.getHeadLastname());
////        existingSchool.setHeadEmail(school.getHeadEmail());
////        existingSchool.setHeadPhonenumber(school.getHeadPhonenumber());
////        existingSchool.setAdminFirstname(school.getAdminFirstname());
////        existingSchool.setAdminLastName(school.getAdminLastName());
////        existingSchool.setAdminEmail(school.getAdminEmail());
////        existingSchool.setAdminPhonenumber(school.getAdminPhonenumber());
////        existingSchool.setNumberOfStudents(school.getNumberOfStudents());
////        existingSchool.setNumberOfTeachers(school.getNumberOfTeachers());
////        existingSchool.setAdminNote(school.getAdminNote());
////        existingSchool.setResponsibleAuthority(school.getResponsibleAuthority());
////        existingSchool.setOwnershipType(school.getOwnershipType());
////        existingSchool.setPaymentMethod(school.getPaymentMethod());
////        existingSchool.setDepartments(school.getDepartments());
////        existingSchool.setSchoolClass(school.getSchoolClass());
////        existingSchool.setLevelGrade(school.getLevelGrade());
////        existingSchool.setSubjects(school.getSubjects());
////        existingSchool.setSchoolTerms(school.getSchoolTerms());
//////        existingSchool.setAcademicYears(school.getAcademicYears());
////        existingSchool.setStatus(school.getStatus());
////
////
////        if (receiptFile != null && ! receiptFile.isEmpty()) {
////            String receiptFilename = saveReceipt(receiptFile);
////            school.setReceipt(receiptFilename);
////        }
////
////
////
////        School updateSchool  = schoolRepository.save(existingSchool);
////        return updateSchool;
////    }
//
//
//
//
//
//
//    @Override
//    public Page<School > getAllSchoolsByDistrict(String district, int page, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<School>  schoolsPage = schoolRepository.findByDistrict(district, pageable);
//
//        List<School> filteredSchools = new ArrayList<>(schoolsPage.getContent());
//
//        Page<School> pages = new PageImpl<>(filteredSchools, pageable, schoolsPage.getTotalElements());
//
//        return pages;
//    }
//
//    public void validateGradingScales(List<GradingScale> newScales, List<GradingScale> existingScales) {
//        // Combine existing and new scales for validation
//        List<GradingScale> allScales = new ArrayList<>(existingScales);
//        allScales.addAll(newScales);
//
//        // Sort by minScore
//        allScales.sort(Comparator.comparingDouble(GradingScale::getMinScore));
//
//        // Check for overlapping ranges
//        for (int i = 0; i < allScales.size() - 1; i++) {
//            GradingScale current = allScales.get(i);
//            GradingScale next = allScales.get(i + 1);
//
//            if (current.getMaxScore() > next.getMinScore()) {
//                throw new IllegalArgumentException("Grading scales overlap between "
//                        + current.getGrade() + " and " + next.getGrade());
//            }
//        }
//
//        // Check for duplicate grades
//        Set<String> grades = new HashSet<>();
//        for (GradingScale scale : allScales) {
//            if (!grades.add(scale.getGrade())) {
//                throw new IllegalArgumentException("Duplicate grade: " + scale.getGrade());
//            }
//        }
//    }
//
//
//
//    @Override
//    @Transactional
//    public LevelGrade insertGradingScale(Long schoolId, UUID levelGradeId, List<GradingScale> gradingScale) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//
//       LevelGrade levelGrade = existingSchool.getLevelGrade().stream().filter(level -> levelGradeId.equals(level.getLevelGradeId()))
//               .findAny()
//               .orElseThrow(() -> new RuntimeException("Level with the specified id not found"));
//
//
//        List<GradingScale> existingScales = levelGrade.getGradingScales();
//
//        // Validate against existing scales
//        validateGradingScales(gradingScale, existingScales);
//        levelGrade.setGradingScales(gradingScale);
//
//        schoolRepository.save(existingSchool);
//        return levelGrade;
//    }
//
////    public LevelGrade updateGradingScale(Long schoolId, UUID levelGradeId, Map<String, Integer> gradingScale) {
////        School existingSchool = schoolRepository.findById(schoolId)
////                .orElseThrow(() -> new RuntimeException("School not found"));
////
////        LevelGrade levelGrade = existingSchool.getLevelGrade().stream().filter(level -> levelGradeId.equals(level.getLevelGradeId()))
////                .findAny()
////                .orElseThrow(() -> new RuntimeException("Level with the specified id not found"));
////        levelGrade.setGradingScale(gradingScale);
////        schoolRepository.save(existingSchool);
////        return levelGrade;
////    }
//
//
//    @Override
//    public Page<School> getAllSchoolsByTown(String town, int page, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<School>  schoolsPage = schoolRepository.findByTown(town, pageable);
//
//        List<School> filteredSchools = new ArrayList<>(schoolsPage.getContent());
//
//        Page<School> pages = new PageImpl<>(filteredSchools, pageable, schoolsPage.getTotalElements());
//
//        return pages;
//    }
//
//    @Override
//    public Page<School > getAllSchoolsByProvince(String province, int page, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<School>  schoolsPage = schoolRepository.findByProvince(province, pageable);
//
//        List<School> filteredSchools = new ArrayList<>(schoolsPage.getContent());
//
//        Page<School> pages = new PageImpl<>(filteredSchools, pageable, schoolsPage.getTotalElements());
//
//        return pages;
//    }
//
//
//    @Override
//    public Page<School> getAllSchoolsByCountry(String country, int page, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<School>  schoolsPage = schoolRepository.findByCountry(country, pageable);
//
//        List<School> filteredSchools = new ArrayList<>(schoolsPage.getContent());
//
//        Page<School> pages = new PageImpl<>(filteredSchools, pageable, schoolsPage.getTotalElements());
//
//        return pages;
//    }
//
//
//    @Override
//    public Page<School> getAllSchoolsByStatus(Status status, int page, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<School>  schoolsPage = schoolRepository.findByStatus(status, pageable);
//
//        List<School> filteredSchools = new ArrayList<>(schoolsPage.getContent());
//
//        Page<School> pages = new PageImpl<>(filteredSchools, pageable, schoolsPage.getTotalElements());
//
//        return pages;
//    }
//
//
//    @Override
//    public Page<School> getAllSchoolsByOwnershipType(OwnershipType ownershipType, int page, int size) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Page<School>  schoolsPage = schoolRepository.findByOwnershipType(ownershipType, pageable);
//
//        List<School> filteredSchools = new ArrayList<>(schoolsPage.getContent());
//
//        Page<School> pages = new PageImpl<>(filteredSchools, pageable, schoolsPage.getTotalElements());
//
//        return pages;
//    }
//
//    public School getSchoolById(Long id) {
//        return schoolRepository.findById(id).orElse(null);
//    }
//
//    @Transactional
//    @Override
//    public String addSubjectsToClass(Long schoolId, String classId, List<String> subjectIds) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//
//        UUID classUuid;
//        List<UUID> subjectUuidList = convertToUUIDList(subjectIds);
//
//        try {
//            classUuid = UUID.fromString(classId);
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid UUID format", e);
//        }
//        Set<UUID> existingSubjectIds = existingSchool.getSubjects().stream()
//                .map(Subjects::getSubjectId)
//                .collect(Collectors.toSet());
//
//        List<UUID> invalidSubjects = subjectUuidList.stream()
//                .filter(subjectId -> !existingSubjectIds.contains(subjectId))
//                .toList();
//
//        if (!invalidSubjects.isEmpty()) {
//            throw new RuntimeException("Invalid subject IDs: " + invalidSubjects);
//        }
//
//        SchoolClass schoolClass = existingSchool.getSchoolClass().stream()
//                .filter(sc -> sc.getSchoolClassId().equals(classUuid))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Class not found"));
//// Filter and add valid subjects
//        Set<UUID> currentSubjects = schoolClass.getSubjects();
//        if (currentSubjects == null) {
//            currentSubjects = new HashSet<>();
//            schoolClass.setSubjects(currentSubjects);
//        }
//
//        subjectUuidList.stream()
//                .filter(existingSubjectIds::contains)
//                .forEach(currentSubjects::add);
//
//        schoolRepository.save(existingSchool);
//
//        return "Class with id: " + classId + " is successfully updated with subjects.";
//    }
//
//    public List<UUID> convertToUUIDList(List<String> stringList) {
//        return stringList.stream()
//                .map(str -> {
//                    try {
//                        return UUID.fromString(str);
//                    } catch (IllegalArgumentException e) {
//                        // Handle the invalid UUID case, e.g., log the error or return null
//                        System.out.println("Invalid UUID: " + str);
//                        return null; // Or handle differently
//                    }
//                })
//                .filter(uuid -> uuid != null) // Remove nulls from the list
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    @Override
//    public String addDepartmentToClass(Long schoolId, String classId, String departmentId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//        UUID classUuid;
//        UUID departmentUuid;
//
//        try {
//            classUuid = UUID.fromString(classId);
//            departmentUuid = UUID.fromString(departmentId);
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid UUID format", e);
//        }
//
//        existingSchool.getSchoolClass().stream()
//                .filter(sc -> sc.getSchoolClassId().equals(classUuid))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Class not found"))
//                .setDepartmentId(departmentUuid);
//        ;
//
//        schoolRepository.save(existingSchool);
//
//        return "Class with id:: "+ classId + " is successfully added to department with id:: "+ departmentId;
//    }
//
//    @Transactional
//    @Override
//    public String addSubjectToClass(Long schoolId, String classId, String subjectId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//
//        UUID classUuid;
//        UUID subjectUuid;
//
//        try {
//            classUuid = UUID.fromString(classId);
//            subjectUuid = UUID.fromString(subjectId);
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid UUID format", e);
//        }
//
//       boolean isPresent = existingSchool.getSubjects().stream()
//                .anyMatch(subject -> subject.getSubjectId().equals(subjectUuid))
//                ;
//
//        if (!isPresent) {
//            throw new RuntimeException("Invalid subject ID: " + subjectId);
//        }
//
//        existingSchool.getSchoolClass().stream()
//                .filter(sc -> sc.getSchoolClassId().equals(classUuid))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Class not found"))
//                .getSubjects().add(subjectUuid)
//        ;
//
//        schoolRepository.save(existingSchool);
//
//        return "Subject with id:: "+ subjectId + " is successfully added to class with id:: "+ classId;
//    }
//
//
//    @Transactional
//    @Override
//    public List<Department> addDepartment(Long schoolId, List<Department> departmentList) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//
//        existingSchool.getDepartments().addAll(departmentList);
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getDepartments();
//    }
//
//    @Override
//    public List<Department> getDepartmentsBySchool(Long schoolId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        return existingSchool.getDepartments();
//    }
//
//    @Override
//    public Department getDepartmentsBySchool(Long schoolId, String deptName) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        return existingSchool.getDepartments().stream().filter(d -> deptName.equalsIgnoreCase(d.getName())).findFirst().orElseThrow(() -> new DepartmentNotFound("Department "+deptName+" not found"));
//    }
//
//    @Transactional
//    @Override
//    public List<Department> editDepartmentByName(Long schoolId, String currentName, Department updatedDepartment) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//
//        for (Department department : existingSchool.getDepartments()) {
//            if (department.getName().equals(currentName)) {
//                copyNonNullProperties(updatedDepartment, department);
//                break;
//            }
//        }
//
//        schoolRepository.save(existingSchool);
//        return existingSchool.getDepartments();
//    }
//
//    @Transactional
//    @Override
//    public List<Department> removeDepartmentByName(Long schoolId, String name) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//
//        existingSchool.getDepartments().removeIf(department -> department.getName().equals(name));
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getDepartments();
//    }
//
//
//
//    private SchoolClass mapToSchoolCLass(SchoolClassRequest schoolClassRequest){
//        UUID levelUuid;
//
//        try {
//            levelUuid = UUID.fromString(schoolClassRequest.getLevelId());
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid UUID format", e);
//        }
//        return SchoolClass.builder()
//                .name(schoolClassRequest.getName())
//                .description(schoolClassRequest.getDescription())
//                .schoolClassId(UUID.randomUUID())
//                .levelId(levelUuid)
//                .build();
//    }
//    @Transactional
//    @Override
//    public List<SchoolClass> addSchoolClass(Long schoolId, List<SchoolClassRequest> schoolClassList) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new RuntimeException("School not found"));
//
//        List<SchoolClass> schoolClasses = schoolClassList.stream()
//                .map(this::mapToSchoolCLass)
//                .peek(schoolClass -> {
//                    schoolClass.setSchool(existingSchool); // Set the actual School object
//                })
//                .toList();
//
//        schoolClasses.forEach(schoolClass -> {
//            existingSchool.getSchoolClass().add(schoolClass);
//        });
//
//
//        return schoolRepository.save(existingSchool).getSchoolClass();
//    }
//
//    @Transactional
//    @Override
//    public List<SchoolClass> editSchoolClassByName(Long schoolId, String currentName, SchoolClass updatedSchoolClass) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() ->  new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        for (SchoolClass schoolClass : existingSchool.getSchoolClass()) {
//            if (schoolClass.getName().equals(currentName)) {
//                copyNonNullProperties(updatedSchoolClass, schoolClass);
//                break;
//            }
//        }
//
//        schoolRepository.save(existingSchool);
//        return existingSchool.getSchoolClass();
//    }
//
//
//    @Override
//    public SchoolClass gettSchoolClassByName(Long schoolId, String name) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() ->  new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        return existingSchool.getSchoolClass().stream().filter(c -> name.equalsIgnoreCase(c.getName())).findFirst().orElseThrow(() -> new EntityNotFoundException("Class "+name+" not found"));
//    }
//
//
//    @Transactional
//    @Override
//    public List<SchoolClass> removeSchoolClassByName(Long schoolId, String name) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        existingSchool.getSchoolClass().removeIf(schoolClass -> schoolClass.getName().equals(name));
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getSchoolClass();
//    }
//
//
//    @Transactional
//    @Override
//    public List<LevelGrade> addLevelGrade(Long schoolId, LevelGradeRequest request) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        validateGradingScales(request.getGradingScales());
//
//        LevelGrade levelGrade = new LevelGrade();
//        levelGrade.setName(request.getName());
//        levelGrade.setDescription(request.getDescription());
//        levelGrade.setGradingScales(request.getGradingScales());
//        levelGrade.setSchool(existingSchool);
//
//        existingSchool.getLevelGrade().add(levelGrade);
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getLevelGrade();
//    }
//
//    private void validateGradingScales(List<GradingScale> gradingScales) {
//        gradingScales.sort(Comparator.comparingDouble(GradingScale::getMinScore));
//
//        for (int i = 0; i < gradingScales.size() - 1; i++) {
//            GradingScale current = gradingScales.get(i);
//            GradingScale next = gradingScales.get(i + 1);
//
//            if (current.getMaxScore() > next.getMinScore()) {
//                throw new IllegalArgumentException("Grading scales overlap between "
//                        + current.getGrade() + " and " + next.getGrade());
//            }
//        }
//
//        Set<String> grades = new HashSet<>();
//        for (GradingScale scale : gradingScales) {
//            if (!grades.add(scale.getGrade())) {
//                throw new IllegalArgumentException("Duplicate grade: " + scale.getGrade());
//            }
//        }
//    }
//
//
//    @Transactional
//    @Override
//    public List<LevelGrade> editLevelGradeByName(Long schoolId, String currentName, LevelGrade updatedLevelGrade) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        for (LevelGrade levelGrade : existingSchool.getLevelGrade()) {
//            if (levelGrade.getName().equals(currentName)) {
//                copyNonNullProperties(updatedLevelGrade, levelGrade);
//                break;
//            }
//        }
//
//        schoolRepository.save(existingSchool);
//        return existingSchool.getLevelGrade();
//    }
//
//    @Transactional
//    @Override
//    public List<LevelGrade> removeLevelGradeByName(Long schoolId, String name) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        existingSchool.getLevelGrade().removeIf(levelGrade -> levelGrade.getName().equals(name));
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getLevelGrade();
//    }
//
//
//    @Transactional
//    @Override
//    public List<Subjects> addSubject(Long schoolId, List<Subjects> subjectList) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        existingSchool.getSubjects().addAll(subjectList);
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getSubjects();
//    }
//
//    @Transactional
//    @Override
//    public List<Subjects> editSubjectByName(Long schoolId, String currentName, Subjects updatedSubject) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        for (Subjects subject : existingSchool.getSubjects()) {
//            if (subject.getSubjectName().equals(currentName)) {
//                copyNonNullProperties(updatedSubject, subject);
//                break;
//            }
//        }
//
//        schoolRepository.save(existingSchool);
//        return existingSchool.getSubjects();
//    }
//
//    @Override
//    public Subjects getSubjectByName(Long schoolId, String name) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        return existingSchool.getSubjects().stream().filter(s -> name.equalsIgnoreCase(s.getSubjectName())).findFirst().orElseThrow(() -> new SubjectNotFoundException("Subject "+ name+" not found!"));
//    }
//
//    @Override
//    public List<Subjects> getAllSubjectsBySchool(Long schoolId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        return existingSchool.getSubjects();
//    }
//
//
//    @Transactional
//    @Override
//    public List<Subjects> removeSubjectByName(Long schoolId, String name) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        existingSchool.getSubjects().removeIf(subject -> subject.getSubjectName().equals(name));
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getSubjects();
//    }
//
//
//    @Transactional
//    @Override
//    public List<SchoolTerm> addSchoolTerm(Long schoolId, List<SchoolTerm> schoolTermList) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        existingSchool.getSchoolTerms().addAll(schoolTermList);
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getSchoolTerms();
//    }
//
//    @Override
//    public List<SchoolTerm> getSchoolAllSchoolTerms(Long schoolId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        return existingSchool.getSchoolTerms();
//    }
//
//    @Override
//    public SchoolTerm getSchoolAllSchoolTermByName(Long schoolId, String termName) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        return existingSchool.getSchoolTerms().stream().filter(e -> termName.equalsIgnoreCase(e.getName())).findFirst().orElseThrow(() -> new TermNotFoundException("Term "+termName+" not found."));
//    }
//
//    @Transactional
//    @Override
//    public List<SchoolTerm> editSchoolTermByName(Long schoolId, String currentName, SchoolTerm updatedSchoolTerm) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        for (SchoolTerm schoolTerm : existingSchool.getSchoolTerms()) {
//            if (schoolTerm.getName().equals(currentName)) {
//                copyNonNullProperties(updatedSchoolTerm, schoolTerm);
//                break;
//            }
//        }
//
//        schoolRepository.save(existingSchool);
//        return existingSchool.getSchoolTerms();
//    }
//
//    @Transactional
//    @Override
//    public List<SchoolTerm> removeSchoolTermByName(Long schoolId, String name) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        existingSchool.getSchoolTerms().removeIf(schoolTerm -> schoolTerm.getName().equals(name));
//        schoolRepository.save(existingSchool);
//
//        return existingSchool.getSchoolTerms();
//    }
//
//    @Transactional
//    @Override
//    public List<AcademicYear> addAcademicYear(Long schoolId, List<AcademicYear> academicYearList) {
////        School existingSchool = schoolRepository.findById(schoolId)
////                .orElseThrow(() -> new RuntimeException("School not found"));
////
////        existingSchool.getAcademicYears().addAll(academicYearList);
////        schoolRepository.save(existingSchool);
////
////        return existingSchool.getAcademicYears();
//
//        return null;
//    }
//
//    @Transactional
//    @Override
//    public List<AcademicYear> editAcademicYearByName(Long schoolId, String currentName, AcademicYear updatedAcademicYear) {
////        School existingSchool = schoolRepository.findById(schoolId)
////                .orElseThrow(() -> new RuntimeException("School not found"));
////
////        for (AcademicYear academicYear : existingSchool.getAcademicYears()) {
////            if (academicYear.getName().equals(currentName)) {
////                copyNonNullProperties(updatedAcademicYear, academicYear);
////                break;
////            }
////        }
////
////        schoolRepository.save(existingSchool);
////        return existingSchool.getAcademicYears();
//
//        return null;
//    }
//
//    @Transactional
//    @Override
//    public List<AcademicYear> removeAcademicYearByName(Long schoolId, String name) {
////        School existingSchool = schoolRepository.findById(schoolId)
////                .orElseThrow(() -> new RuntimeException("School not found"));
////
////        existingSchool.getAcademicYears().removeIf(academicYear -> academicYear.getName().equals(name));
////        schoolRepository.save(existingSchool);
////
////        return existingSchool.getAcademicYears();
//        return null;
//    }
//
//    @Override
//    public boolean existsBySubjectIdAndClassId(Long schoolId, UUID classId, UUID subjectId, UUID lvl, UUID term) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        // Check if the classId exists in the school's classes
////        boolean classExists = existingSchool.getClasses().stream()
////                .anyMatch(schoolClass -> schoolClass.getId().equals(classId));
//
//        boolean classExists = existingSchool.getSchoolClass().stream()
//                .map(SchoolClass::getSchoolClassId)
//
//                .anyMatch(class_id -> class_id.equals(classId));
//      //  System.out.println("class:: "+ classExists);
//        // Check if the subjectId exists in the school's subjects
////        boolean subjectExists = existingSchool.getSubjects().stream()
////                .anyMatch(subject -> subject.getSubjectId().equals(subjectId));
//
////        existingSchool.getSubjects().stream()
////                .map(Subjects::getSubjectId)
////                .forEach(System.out::println);
//        boolean subjectExists = existingSchool.getSubjects().stream()
//                .map(Subjects::getSubjectId)
//
//                .anyMatch(subject_id -> subject_id.equals(subjectId));
//
//        // existingSchool.getLevelGrade().stream().filter(level -> levelUuid.equals(level.getLevelGradeId()))
//        boolean levelExists = existingSchool.getLevelGrade().stream()
//                .map(LevelGrade::getLevelGradeId)
//                .anyMatch(level -> level.equals(lvl));
//
//       // System.out.println(existingSchool.getLevelGrade());
//
//        boolean termExists = existingSchool.getSchoolTerms().stream()
//                .map(SchoolTerm::getSchoolTermId)
//                .anyMatch(t -> t.equals(term));
//
//      //  System.out.println("subject:: "+ subjectExists);
//        // Return true only if both the class and subject exist
//        System.out.println(levelExists);
//        System.out.println(termExists);
//        System.out.println(subjectExists);
//        System.out.println(classExists);
//        return classExists && subjectExists && levelExists && termExists;
//    }
//
//    @Override
//    public boolean existsByDepartmentId(Long schoolId, UUID departmentId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        // Check if the classId exists in the school's classes
//        // Return true only if both the class and subject exist
//        return existingSchool.getDepartments().stream()
//                .anyMatch(department -> department.getDepartmentId().equals(departmentId));
//    }
//
//    @Override
//    public boolean existsByClassId(Long schoolId, String classId, String levelId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        UUID classUuid;
//        UUID levelUuid;
//
//        try {
//            classUuid = UUID.fromString(classId);
//            levelUuid = UUID.fromString(levelId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDException("Invalid UUID format");
//        }
//
//        boolean classExists = existingSchool.getSchoolClass().stream()
//                .map(SchoolClass::getSchoolClassId)
//                .anyMatch(classUuid::equals);
//
//        boolean levelExists = existingSchool.getLevelGrade().stream()
//                .map(LevelGrade::getLevelGradeId)
//                .anyMatch(levelUuid::equals);
//
//        return classExists && levelExists;
//    }
//
//    @Override
//    public boolean existsByTermId(Long schoolId, String termId) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        UUID termUuid;
//
//        try {
//          termUuid = UUID.fromString(termId);
//        } catch (IllegalArgumentException e) {
//            throw new InvalidUUIDException("Invalid UUID format");
//        }
//        return existingSchool.getSchoolTerms().stream()
//                .anyMatch(term -> term.getSchoolTermId().equals(termUuid));
//
//    }
//
//    @Override
//    public String getGradingScale(Long schoolId, UUID levelUuid, Double score) {
//        School existingSchool = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id:: "+schoolId+" not found"));
//
//        LevelGrade levelGrade = existingSchool.getLevelGrade().stream().filter(level -> levelUuid.equals(level.getLevelGradeId()))
//                .findAny()
//                .orElseThrow(() -> new LevelNotFoundException("Level with the specified id not found"));
//
//
//        return levelGrade.getGradingScales()
//                .stream()
//                .filter(scale -> score >= scale.getMinScore() && score <= scale.getMaxScore())
//                .map(GradingScale::getGrade)
//                .findFirst()
//                .orElse("F");
//    }
//
//    public void copyNonNullProperties(Object source, Object target) {
//        BeanWrapper src = new BeanWrapperImpl(source);
//        Set<String> ignoreSet = new HashSet<>();
//
//        for (java.beans.PropertyDescriptor pd : src.getPropertyDescriptors()) {
//            Object srcValue = src.getPropertyValue(pd.getName());
//            if (srcValue == null) {
//                ignoreSet.add(pd.getName());
//            }
//            System.out.println("source:: "+ srcValue);
//        }
//
//        // Add fields to ignore regardless of their value
//        ignoreSet.add("departments");
//        ignoreSet.add("schoolClass");
//        ignoreSet.add("levelGrade");
//        ignoreSet.add("subjects");
//        ignoreSet.add("schoolTerms");
//        ignoreSet.add("academicYears");
//
//
//
//        BeanUtils.copyProperties(source, target, ignoreSet.toArray(new String[0]));
//    }
//
//
//
//
//    // Method to find level ID by school ID and level name
////    public UUID findLevelId(Long schoolId, String levelName) {
////        School school = schoolRepository.findById(schoolId)
////                .orElseThrow(() -> new SchoolNotFoundException("School with id " + schoolId + " not found"));
////
////        System.out.println("This is the level name" + levelName);
////
////        return school.getLevelGrade().stream()
////                .filter(level -> levelName.equals(level.getName()))
////                .map(LevelGrade::getLevelGradeId)
////                .findFirst()
////                .orElseThrow(() -> new LevelNotFoundException("Level " + levelName + " not found for schoolId: " + schoolId));
////    }
//
//
//    public UUID findLevelId(Long schoolId, String levelName) {
//        String jpql = "SELECT new LevelIdRequest(lg.levelGradeId) FROM LevelGrade lg WHERE lg.school.id = :schoolId AND lg.name = :levelName";
//        LevelIdRequest levelIdRequest = entityManager.createQuery(jpql, LevelIdRequest.class)
//                .setParameter("schoolId", schoolId)
//                .setParameter("levelName", levelName)
//                .getSingleResult();
//
//        return levelIdRequest.getLevelGradeId();
//    }
//
//    // Method to find class ID by school ID, class name, and level ID
//    public UUID findClassId(Long schoolId, String className, UUID levelId) throws ClassNotFoundException {
//        School school = schoolRepository.findById(schoolId)
//                .orElseThrow(() -> new SchoolNotFoundException("School with id " + schoolId + " not found"));
//
//        return school.getSchoolClass().stream()
//                .filter(clazz -> className.equals(clazz.getName()) && levelId.equals(clazz.getLevelId())) // Assuming LevelID is part of SchoolClass
//                .map(SchoolClass::getSchoolClassId)
//                .findFirst()
//                .orElseThrow(() -> new ClassNotFoundException("Class " + className + " not found for levelId: " + levelId));
//    }



}

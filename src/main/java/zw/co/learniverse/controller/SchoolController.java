package zw.co.learniverse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.entities.School;
import zw.co.learniverse.payload.request.SchoolRequest;
import zw.co.learniverse.payload.response.ApiResponse;
import zw.co.learniverse.payload.response.SchoolResponse;
import zw.co.learniverse.service.SchoolService;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Tag(name = "SCHOOL ENDPOINTS", description = "The School APIs. Contains operations like create School, find School by id, find School by country, add school term, school departments etc.")
@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Slf4j

public class SchoolController {

    private final SchoolService schoolService;


@PostMapping(value = "/create", consumes = "multipart/form-data")
@Operation(summary = "Endpoint to Enroll School", description = "This endpoint will allow the school to enter their details")
@PreAuthorize("hasAuthority('WRITE_PRIVILEGE') and hasAnyRole('ADMIN')")
public ResponseEntity<School> createSchool(@ModelAttribute SchoolRequest schoolRequest) {
    try {
        // Create the school using the service
        School createdSchool = schoolService.createSchool(schoolRequest);
        return new ResponseEntity<>(createdSchool, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace(); // Log the exception for debugging
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}


//        @PutMapping(value = "/update/{schoolId}", consumes = "multipart/form-data")
//        @Operation(summary = "Update School", description = "Update school details by id. This can also be used by super admin to approve by setting status to active and posting details required to create admin in the suer service")
//        @PreAuthorize("hasAuthority('WRITE_PRIVILEGE') and hasAnyRole('SUPER_ADMIN','ADMIN')")
//        public ResponseEntity<ApiResponse<School>> updateSchool(
//                @PathVariable Long schoolId,
//                @ModelAttribute SchoolRequest updatedSchoolRequest,
//                @RequestParam(required = false) MultipartFile logo,
//                @RequestParam(required = false) MultipartFile receipt,
//                @RequestHeader("Authorization") String authorizationValue) throws IOException {
//
//            log.info("Updating School with ID: {}", schoolId);
//
//            System.out.println(updatedSchoolRequest);
//
//            // Call the service to update the school with all required parameters
//            School updatedSchoolEntity = schoolService.update(schoolId, updatedSchoolRequest, logo, receipt, authorizationValue.substring(7));
//
//        // Create the response object
//        ApiResponse<School> response = new ApiResponse<>("School updated successfully", updatedSchoolEntity);
//
//        // Return the response entity with status and body
//        return ResponseEntity.ok(response);
//    }
//
//
//
//    @GetMapping()
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN','SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Test endpoint", description = "Test endpoint")
//    public String get(@RequestHeader("loggedInUser") String username){
//        return username;
//    }
//
//    @GetMapping("/{schoolId}/classes")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "List All The CLASSES", description = "Get a list of all the classes at that particular school")
//    public List<ClassResponse> getAllClasses(@PathVariable("schoolId") Long schoolId){
//        return schoolService.getAllClassesFromSchool(schoolId);
//    }
//
//    @GetMapping("/{schoolId}/class")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get Class Details By Entering Its Name", description = "Enter the school id and class name and get  it details like level id, department details, subjects in that class")
//    public SchoolClass getClass(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String className
//            ){
//        return schoolService.gettSchoolClassByName(schoolId, className);
//    }
//
//    @GetMapping("/{schoolId}/departments")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get All Departments At That Particular School", description = "Use School Id to get the list of all the departments")
//    public List<Department> getAllDepartments(@PathVariable("schoolId") Long schoolId){
//        return schoolService.getDepartmentsBySchool(schoolId);
//    }
//
//    @GetMapping("/{schoolId}/terms")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get All Terms At That Particular School", description = "Use School Id to get the list of all the terms created at that particular school")
//    public List<SchoolTerm> getAllTerms(@PathVariable("schoolId") Long schoolId){
//        return schoolService.getSchoolAllSchoolTerms(schoolId);
//    }
//
//    @GetMapping("/{schoolId}/department")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get Department Details By Entering Its Name", description = "Enter the school id and department name and get  it's details")
//    public Department getDepartmentByName(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String departmentName){
//        return schoolService.getDepartmentsBySchool(schoolId, departmentName);
//    }
//
//    @GetMapping("/{schoolId}/term")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get Term Details By Entering Its Name", description = "Enter the school id and term name and get  it's details")
//    public SchoolTerm getTermByName(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String termName){
//        return schoolService.getSchoolAllSchoolTermByName(schoolId, termName);
//    }
//
//    @GetMapping("/{schoolId}/subject")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get Subject Details By Entering Its Name", description = "Enter the school id and subject name and get  it's details")
//    public Subjects getSubjectByName(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String subjectName){
//        return schoolService.getSubjectByName(schoolId, subjectName);
//    }
//
//    @GetMapping("/{schoolId}/subjects")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get All Subjects at that school", description = "Enter the school id and get all subjects at that particular school")
//    public List<Subjects> getSubjects(
//            @PathVariable("schoolId") Long schoolId){
//        return schoolService.getAllSubjectsBySchool(schoolId);
//    }
//
//    @GetMapping("/{schoolId}/level")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get Level Details By Entering Its Name", description = "Enter the school id and level name and get  it's details")
//    public LevelGrade getLevelsByName(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String levelName){
//        return schoolService.getAllLevelsFromSchoolByName(schoolId, levelName);
//    }
//
//    @GetMapping("/{schoolId}/levels")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get All Levels By Entering School Id", description = "Enter the school id and get all the levels at that particular school")
//    public List<LevelGrade> getLevels(
//            @PathVariable("schoolId") Long schoolId){
//        return schoolService.getAllLevelsFromSchool(schoolId);
//    }
//
//    @GetMapping("/headers")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_SUPERADMIN')")
//    @Operation(summary = "For Testing", description = "For Testing")
//    public String getHeaders(@RequestHeader Map<String, String> headers) {
//        StringBuilder headerInfo = new StringBuilder();
//        headers.forEach((key, value) -> headerInfo.append(key).append(": ").append(value).append("\n"));
//        return headerInfo.toString();
//    }
//
//    @GetMapping("/schools")
//   @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN', 'SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Get A List Of All The Schools", description = "Fetch all schools in the database")
//    public List<SchoolResponse> getAllSchools(){
//        return schoolService.getAllSchools();
//    }
//
//    // /
//    @PutMapping("/{schoolId}/class/{classId}/department/{departmentId}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Add a class to department", description = "Add a particular class to department using classId, schoolId and departmentId")
//    public String addDepartmentToClass(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("classId") String classId,
//            @PathVariable("departmentId") String departmentId
//    ){
//        return schoolService.addDepartmentToClass(schoolId,classId,departmentId);
//    }
//    //insertGradingScale(Long schoolId, UUID levelGradeId, Map<String, Integer> gradingScale);
//
//    @PutMapping("/{schoolId}/level/{levelId}/grading-scale")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Add Grading Scale To Level", description = "Add grading scale to a particular level")
//    public LevelGrade insertGradeScaleToLevel(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("levelId") String levelId,
//            @RequestBody List<GradingScale> gradingScale
//    ){
//        UUID levelUuid;
//
//        try {
//            levelUuid = UUID.fromString(levelId);
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid UUID format", e);
//        }
//        return schoolService.insertGradingScale(schoolId, levelUuid,  gradingScale);
//    }
//
//    @GetMapping("/{schoolId}/level/{levelId}/score/{score}/grading-scale")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Fetch Grade Based on Level And School", description = "Getting the grade for a school based on the level and school grading created")
//    public String getGradeScaleForLevel(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("levelId") String levelId,
//            @PathVariable("score") Double score
//    ){
//        UUID levelUuid;
//
//        try {
//            levelUuid = UUID.fromString(levelId);
//        } catch (IllegalArgumentException e) {
//            throw new RuntimeException("Invalid UUID format", e);
//        }
//        return schoolService.getGradingScale(schoolId, levelUuid, score);
//    }
//
//    @PutMapping("/{schoolId}/class/{classId}/subjects")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Add Subjects To A Class", description = "A List of subjects can be added to a class at once")
//    @ResponseStatus(HttpStatus.OK)
//    public String addSubjectsToClass(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("classId") String classId,
//            @RequestBody List<String> subjectIds
//    ){
//        return schoolService.addSubjectsToClass(schoolId,classId,subjectIds);
//    }
//
//    @PutMapping("/{schoolId}/class/{classId}/subject/{subjectId}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Add One Subject To A Class", description = "Add only one subject to a class")
//    @ResponseStatus(HttpStatus.OK)
//    public String addSubjectToClass(
//            @PathVariable("schoolId") Long schoolId,
//            @PathVariable("classId") String classId,
//            @PathVariable("subjectId") String subjectId
//    ){
//        return schoolService.addSubjectToClass(schoolId,classId,subjectId);
//    }
//
//    @PostMapping("/sub_class_exists")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Check existence of subject and class", description = "Check if subject there at that school and check if class is there at that school")
//    public boolean existsBySubjectIdAndClassId(@RequestBody SubjectClassRequest subjectClassRequest){
//        return schoolService.existsBySubjectIdAndClassId(subjectClassRequest.getSchoolId(),subjectClassRequest.getClassId(), subjectClassRequest.getSubjectId(),subjectClassRequest.getLevelId(),subjectClassRequest.getTermId());
//    }
//
//    @PostMapping("/department_exists")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Check if department is found at that particular school", description = "Check if department exists at that school")
//    public boolean existsByDepartmentId(@RequestBody DepartmentExistsRequest departmentExistsRequest){
//       // System.out.println(departmentExistsRequest);
//        return schoolService.existsByDepartmentId(departmentExistsRequest.getSchoolId(),departmentExistsRequest.getDepartmentId());
//    }
//
//
//    @GetMapping(value = "/{schoolId}/level/{levelId}/class/{classId}/exists")
//    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Use class id to find if it is found at that school", description = "Use class id to check if class is available at that school")
//    public boolean existsByCLassId(@PathVariable("schoolId") Long schoolId,
//                                                  @PathVariable("classId") String classId,
//                                   @PathVariable("levelId") String levelId
//                                                  ){
//        return schoolService.existsByClassId(schoolId,classId, levelId);
//    }
//
//    //"/api/v1/schools/"+existingStudent.getSchoolId()+"/term/"+termId+"/exists"
//
//    @GetMapping(value = "/{schoolId}/term/{termId}/exists")
//    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    public boolean existsByTermId(@PathVariable("schoolId") Long schoolId,
//                                   @PathVariable("termId") String termId
//    ){
//        return schoolService.existsByTermId(schoolId,termId);
//    }
//
//    @GetMapping("/district/{district}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Find all schools by district", description = "Get list of all the schools in that district")
//    public Page<School> getAllSchoolsForDistrict(
//            @PathVariable("district") String district,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        return schoolService.getAllSchoolsByDistrict(district, page, size);
//    }
//
//
//    @GetMapping("/town/{town}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Find all schools by town", description = "Get list of all the schools in that town")
//    public Page<School> getAllSchoolsForTown(
//            @PathVariable("town") String town,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        return schoolService.getAllSchoolsByTown(town, page, size);
//    }
//
//    @GetMapping("/province/{province}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Find all schools by province", description = "Get list of all the schools in that province")
//    public Page<School> getAllSchoolsForProvince(
//            @PathVariable("province") String province,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        return schoolService.getAllSchoolsByProvince(province, page, size);
//    }
//
//
//
//    @GetMapping("/country/{country}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPERADMIN','SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Find all schools by country", description = "Get list of all the schools in that country")
//    public Page<School> getAllSchoolsForCountry(
//            @PathVariable("country") String country,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        return schoolService.getAllSchoolsByCountry(country, page, size);
//    }
//
//
//    @GetMapping("/status/{status}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Find all schools by status", description = "Get list of all the schools in that status")
//    public Page<School> getAllSchoolsForStatus(
//            @PathVariable("status") Status status,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        return schoolService.getAllSchoolsByStatus(status, page, size);
//    }
//
//
//    @GetMapping("/ownershipType/{ownershipType}")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPER_ADMIN')")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(summary = "Find all schools by ownership type", description = "Get list of all the schools in that ownership type")
//    public Page<School> getAllSchoolsForOwnershipType(
//            @PathVariable("ownershipType") OwnershipType ownershipType,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//        return schoolService.getAllSchoolsByOwnershipType(ownershipType, page, size);
//    }
//
//    @GetMapping("/id/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER','SUPER_ADMIN')")
//    @Operation(summary = "Find school by id", description = "Get school details by it's id")
//    public ResponseEntity<School> getSchoolById(@PathVariable Long id) {
//        School school= schoolService.getSchoolById(id);
//        if (school != null) {
//            return ResponseEntity.ok(school);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//
//    @PutMapping("/{schoolId}/add/departments")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Add department to a school", description = "Add a department to a school")
//    public List<Department> addSchoolDepartments(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestBody List<Department> departments
//    ){
//        return schoolService.addDepartment(schoolId, departments);
//    }
//
//    @PutMapping("/update/{schoolId}/departments")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Update school department details", description = "Updatte the details of the school department")
//    public List<Department> updateSchoolDepartment(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name,
//            @RequestBody Department department
//    ){
//        return schoolService.editDepartmentByName(schoolId,name, department);
//    }
//
//    @PutMapping("/{schoolId}/delete/departments")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Delete school department", description = "Delete school department details")
//    public List<Department> deleteSchoolDepartment(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name
//    ){
//        return schoolService.removeDepartmentByName(schoolId,name);
//    }
//
//    @PutMapping("/{schoolId}/add/schoolClasses")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Add class to a school", description = "Create class for a school")
//    public List<SchoolClass> addSchoolClasses(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestBody List<SchoolClassRequest> schoolClasses
//    ){
//        return schoolService.addSchoolClass(schoolId, schoolClasses);
//    }
//
//    @PutMapping("/update/{schoolId}/schoolClass")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Update school class details", description = "Update school class details")
//    public List<SchoolClass> updateSchoolClass(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name,
//            @RequestBody SchoolClass schoolClass
//    ){
//        return schoolService.editSchoolClassByName(schoolId,name, schoolClass);
//    }
//
//    @PutMapping("/{schoolId}/delete/schoolClass")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Remove school from database", description = "Delete school from database")
//    public List<SchoolClass> deleteSchoolClass(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name
//    ){
//        return schoolService.removeSchoolClassByName(schoolId,name);
//    }
//
//    @PutMapping("/{schoolId}/add/levelGrade")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Add levels like Grade 1 or Form 1 etc", description = "Add school levels")
//    public List<LevelGrade> addLevelGrades(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestBody LevelGradeRequest levelGrades
//    ){
//        return schoolService.addLevelGrade(schoolId, levelGrades);
//    }
//
//    @PutMapping("/update/{schoolId}/levelGrade")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Update level or grade details", description = "Update level or grade details for that school")
//    public List<LevelGrade> updateLevelGrade(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name,
//            @RequestBody LevelGrade levelGrade
//    ){
//        return schoolService.editLevelGradeByName(schoolId,name, levelGrade);
//    }
//
//    @PutMapping("/{schoolId}/delete/levelGrade")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Delete level or grade from the database", description = "Delete level or grade for that school")
//    public List<LevelGrade> deleteLevelGrade(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name
//    ){
//        return schoolService.removeLevelGradeByName(schoolId,name);
//    }
//
//    @PutMapping("/{schoolId}/add/subject")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Add several subjects to a school and once", description = "Add multiple subjects at once")
//    public List<Subjects> addSubjects(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestBody List<Subjects> subjects
//    ){
//        return schoolService.addSubject(schoolId, subjects);
//    }
//
//    @PutMapping("/update/{schoolId}/subject")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Update subject details for a school", description = "Update a particular subject for a particular school")
//    public List<Subjects> updateSubject(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name,
//            @RequestBody Subjects subject
//    ){
//        return schoolService.editSubjectByName(schoolId,name, subject);
//    }
//
//    @PutMapping("/{schoolId}/delete/subject")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('SUPER_ADMIN','ROLE_ADMIN')")
//    @Operation(summary = "Delete a subject", description = "Delete a particular subject")
//    public List<Subjects> deleteSubject(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name
//    ){
//        return schoolService.removeSubjectByName(schoolId,name);
//    }
//
//
//
//    @PutMapping("/{schoolId}/add/schoolTerm")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Add several terms at once", description = "Add a list of terms to a school")
//    public List<SchoolTerm> addSchoolTerms(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestBody List<SchoolTerm> schoolTerms
//    ){
//        return schoolService.addSchoolTerm(schoolId, schoolTerms);
//    }
//
//    @PutMapping("/update/{schoolId}/schoolTerm")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Update school term", description = "Update school term details")
//    public List<SchoolTerm> updateSchoolTerm(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name,
//            @RequestBody SchoolTerm schoolTerm
//    ){
//        return schoolService.editSchoolTermByName(schoolId,name, schoolTerm);
//    }
//
//    @PutMapping("/{schoolId}/delete/schoolTerm")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Delete term", description = "Delete school term")
//    public List<SchoolTerm> deleteSchoolTerm(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name
//    ){
//        return schoolService.removeSchoolTermByName(schoolId,name);
//    }
//
//
//    @PutMapping("/{schoolId}/add/academicYear")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Add several academic years at once", description = "Post a list of academic years")
//    public List<AcademicYear> addAcademicYear(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestBody List<AcademicYear> academicYears
//    ){
//        return schoolService.addAcademicYear(schoolId, academicYears);
//    }
//
//    @PutMapping("/update/{schoolId}/academicYear")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Use academic name to update its details", description = "Use academic year to update it's details")
//    public List<AcademicYear> updateAcademicYear(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name,
//            @RequestBody AcademicYear academicYear
//    ){
//        return schoolService.editAcademicYearByName(schoolId,name, academicYear);
//    }
//
//    @PutMapping("/{schoolId}/delete/academicYear")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ROLE_ADMIN')")
//    @Operation(summary = "Delete academic year", description = "Remove academic year")
//    public List<AcademicYear> deleteAcademicYear(
//            @PathVariable("schoolId") Long schoolId,
//            @RequestParam("name") String name
//    ){
//        return schoolService.removeAcademicYearByName(schoolId,name);
//    }
//
//
//    // Endpoint to get Level ID by schoolId and level name
//    @GetMapping("/getLevelId")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Use level name to find the level id", description = "Use level name to find the level id")
//    public ResponseEntity<UUID> getLevelId(@RequestParam Long schoolId, @RequestParam String level) {
//        UUID levelId = schoolService.findLevelId(schoolId, level);
//        return ResponseEntity.ok(levelId);
//    }
//
//    // Endpoint to get Class ID by schoolId, className, and levelId
//    @GetMapping("/getClassId")
//    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasAnyRole('ADMIN', 'TEACHER' ,'STUDENT', 'USER')")
//    @Operation(summary = "Use class name to find the class id", description = "Use class name to find the id of the clas")
//    public ResponseEntity<UUID> getClassId(@RequestParam Long schoolId, @RequestParam String className, @RequestParam UUID levelId) throws ClassNotFoundException {
//        UUID classId = schoolService.findClassId(schoolId, className, levelId);
//        return ResponseEntity.ok(classId);
//    }
//


}

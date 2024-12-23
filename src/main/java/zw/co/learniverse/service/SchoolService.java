package zw.co.learniverse.service;


import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.entities.*;
import zw.co.learniverse.enums.OwnershipType;
import zw.co.learniverse.enums.Status;
import zw.co.learniverse.payload.request.LevelGradeRequest;
import zw.co.learniverse.payload.request.SchoolClassRequest;
import zw.co.learniverse.payload.request.SchoolRequest;
import zw.co.learniverse.payload.response.ClassResponse;
import zw.co.learniverse.payload.response.SchoolResponse;
import zw.co.learniverse.payload.response.SubjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface SchoolService {



   public School createSchool(SchoolRequest schoolRequest) throws io.jsonwebtoken.io.IOException, IOException;

//    List<SubjectResponse> getAllSubjectFromSchool(Long schoolId);
//
//    List<LevelGrade> getAllLevelsFromSchool(Long schoolId);
//
//    LevelGrade getAllLevelsFromSchoolByName(Long schoolId, String levelName);
//
//    List<ClassResponse> getAllClassesFromSchool(Long schoolId);
//
//    public List<SchoolResponse> getAllSchools();
//
//    public School update(Long schoolId, SchoolRequest updatedSchoolRequest, MultipartFile logo, MultipartFile receipt, String authorizationValue) throws io.jsonwebtoken.io.IOException, IOException;
//
//    Page<School> getAllSchoolsByDistrict(String district, int page , int size);
//
//    @Transactional
//    LevelGrade insertGradingScale(Long schoolId, UUID levelGradeId, List<GradingScale> gradingScale);
//
//    Page<School> getAllSchoolsByTown(String town, int page , int size);
//
//    Page<School> getAllSchoolsByProvince(String province, int page , int size);
//
//    Page<School> getAllSchoolsByCountry(String country, int page , int size);
//
//    Page<School> getAllSchoolsByStatus(Status status, int page , int size);
//
//    Page<School> getAllSchoolsByOwnershipType(OwnershipType ownershipType, int page , int size);
//
//    public School getSchoolById(Long id);
//
//
//    @Transactional
//    String addSubjectsToClass(Long schoolId, String classId, List<String> subjectIds);
//
//    @Transactional
//    String addDepartmentToClass(Long schoolId, String classId, String departmentId);
//
//    @Transactional
//    String addSubjectToClass(Long schoolId, String classId, String subjectId);
//
//    @Transactional
//    List<Department> addDepartment(Long schoolId, List<Department> departmentList);
//
//    List<Department> getDepartmentsBySchool(Long schoolId);
//
//    Department getDepartmentsBySchool(Long schoolId, String deptName);
//
//    @Transactional
//    List<Department> editDepartmentByName(Long schoolId, String currentName, Department updatedDepartment);
//    @Transactional
//    List<Department> removeDepartmentByName(Long schoolId, String name);
//
//
//    @Transactional
//    List<SchoolClass> addSchoolClass(Long schoolId, List<SchoolClassRequest> schoolClassList);
//    @Transactional
//    List<SchoolClass> editSchoolClassByName(Long schoolId, String currentName, SchoolClass updatedSchoolClass);
//
//    SchoolClass gettSchoolClassByName(Long schoolId, String name);
//
//    @Transactional
//    List<SchoolClass> removeSchoolClassByName(Long schoolId, String name);
//
//
//    @Transactional
//    List<LevelGrade> addLevelGrade(Long schoolId,  LevelGradeRequest request);
//    @Transactional
//    List<LevelGrade> editLevelGradeByName(Long schoolId, String currentName, LevelGrade updatedLevelGrade);
//    @Transactional
//    List<LevelGrade> removeLevelGradeByName(Long schoolId, String name);
//
//
//    @Transactional
//    List<Subjects> addSubject(Long schoolId, List<Subjects> subjectList);
//    @Transactional
//    List<Subjects> editSubjectByName(Long schoolId, String currentName, Subjects updatedSubject);
//
//    Subjects getSubjectByName(Long schoolId, String name);
//
//    List<Subjects> getAllSubjectsBySchool(Long schoolId);
//
//    @Transactional
//    List<Subjects> removeSubjectByName(Long schoolId, String name);
//
//
//    @Transactional
//    List<SchoolTerm> addSchoolTerm(Long schoolId, List<SchoolTerm> schoolTermList);
//
//    List<SchoolTerm> getSchoolAllSchoolTerms(Long schoolId);
//
//    SchoolTerm getSchoolAllSchoolTermByName(Long schoolId, String termName);
//
//    @Transactional
//    List<SchoolTerm> editSchoolTermByName(Long schoolId, String currentName, SchoolTerm updatedSchoolTerm);
//    @Transactional
//    List<SchoolTerm> removeSchoolTermByName(Long schoolId, String name);
//
//
//    @Transactional
//    List<AcademicYear> addAcademicYear(Long schoolId, List<AcademicYear> academicYearList);
//    @Transactional
//    List<AcademicYear> editAcademicYearByName(Long schoolId, String currentName, AcademicYear updatedAcademicYear);
//    @Transactional
//    List<AcademicYear> removeAcademicYearByName(Long schoolId, String name);
//
//
//    //boolean existsBySubjectIdAndClassId(Long schoolId, UUID classId, UUID subjectId);
//
//    boolean existsByDepartmentId(Long schoolId, UUID departmentId);
//
//    boolean existsByClassId(Long schoolId, String classId, String levelId);
//    boolean existsBySubjectIdAndClassId(Long schoolId, UUID classId, UUID subjectId, UUID lvl, UUID term);
//
//    boolean existsByTermId(Long schoolId, String termId);
//
//    String getGradingScale(Long schoolId, UUID levelUuid, Double score);
//
//
// public UUID findLevelId(Long schoolId, String levelName);
//
// public UUID findClassId(Long schoolId, String className, UUID levelId) throws ClassNotFoundException;


}

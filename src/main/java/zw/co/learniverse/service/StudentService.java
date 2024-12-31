package zw.co.learniverse.service;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zw.co.learniverse.entities.Exam;
import zw.co.learniverse.entities.Student;
import zw.co.learniverse.entities.SubjectClass;
import zw.co.learniverse.enums.Status;
import zw.co.learniverse.payload.request.*;
import zw.co.learniverse.payload.response.ReportCardResponse;
import zw.co.learniverse.payload.response.StudentResponse;


import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    public Student createStudent(StudentRequest studentRequest, String token) throws IOException;


    StudentResponse mapToStudentResponse(Student student);

//    public List<StudentResponse> getAllStudents();
//
//    //public Page<StudentResponse> getAllStudentsByCity(String city, int page, int size);
//
//    StudentResponse updateStudent(Long studentId, StudentUpdateRequest studentUpdateRequest);
//
//    public void uploadCsv(MultipartFile file);
//
//    public List<StudentResponse> findStudents(String firstName, String lastName, String email, Status status);
//
//    StudentResponse findById(Long id);
//
//    public List<StudentResponse> findStudentsByParentEmail(String parentEmail);
//
//    List<StudentResponse> findStudentsByParentPhone(String parentPhone, String regParent);
//
//    public List<StudentResponse> getAllStudentsByParentEmail(String parentEmail);
//
//    StudentResponse getStudentByEmail(String email);
//
//    List<SubjectClass> getAll();
//
//    //assignToClass(studentId,classId,levelId,termId,year,authorizationValue)
//    @Transactional
//    String assignToClass(Long studentId, String classId,String levelId,String termId, Year year, String authorizationValue) throws ClassNotFoundException;
//
//    public Boolean parentExistsByEmail(String parentEmail);
//
//    List<StudentResponse> getAllStudentsByParentPhone(String parentPhone);
//
//    @Transactional
//    List<SubjectClass> assignSubject(Long studentId, SubjectClassRequest subjectClassRequest, String authorizationValue);
//
//    Exam markExamForStudent(Long studentId,
//                            String subjectId,
//                            String classId,
//                            String levelId,
//                            String termId,
//                            Year year,
//                            String examId,
//                            Double mark,
//                            String authValue
//    );
//
//    List<Student> getStudentBySchoolId(Long schoolId);
//
//    List<StudentResponse> getAllStudentsInClass(Long schoolId, String levelId, String classId, String termId, Year year);
//
//    List<StudentResponse> getAllStudentsInSubjectClass(Long schoolId, String levelId, String classId, String termId, Year year, String subjectId);
//
//    List<Student> getStudentBySchoolIdAndClassId(Long schoolId, String classId);
//
//
//    Optional<Student> getStudentWithSubjectsClass(Long studentId);
//
//    @Transactional
//    String assignExamToClass(Long schoolId, String classId, String subjectId, ExamAssignRequest examAssignRequest);
//
//
//    @Transactional
//    String assignExamToClasses(Long schoolId,
//                               List<String> classIds,
//                               String subjectId,
//                               ExamAssignRequest examAssignRequest
//
//    );
//
//    @Transactional
//    String putCommentToStudent(Long schoolId,
//                               Long studentId,
//                               String classId,
//                               String subjectId,
//                               String termId,
//                               String levelId,
//                               Year year,
//                               String comment
//
//    );
//
//    @Transactional
//  public ReportCardResponse generateReportCardForStudent(Long schoolId,
//                                                         Long studentId,
//                                                         String classId,
//                                                         String termId,
//                                                         String levelId,
//                                                         Year year
//    );
//
//    public String uploadExcelFile(MultipartFile file, String authorizationValue) throws IOException;
//
//    List<StudentResponse> getAllStudentsBySchool(Long schoolId);
//
//    Boolean parentExistsByPhoneAndUUID(String parentPhone, String regParent);
//
//
//
//    int countStudentsInClass(Long schoolId, String classId, String levelId, String termId, Year year, String authorizationValue);
//
//
//
//    public String uploadExcelFileClass(MultipartFile file, String authorizationValue) throws IOException;
}

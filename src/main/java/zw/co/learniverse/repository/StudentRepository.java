package zw.co.learniverse.repository;




import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import zw.co.learniverse.entities.Student;




public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Page<Student> findByCountry(String country, Pageable page);
    //Page<Student> findByCity(String city, Pageable pageable);

    @Query("SELECT s FROM Student s JOIN FETCH s.subjectClasses sc WHERE s.id = :studentId")
    Optional<Student> findStudentWithSubjectsCLass(@Param("studentId") Long studentId);

    List<Student> findStudentsByParentPhone(String parentPhone);

    List<Student> findStudentsByParentEmail(String email);

    Optional<Student> findStudentByEmail(String email);

    List<Student>  findStudentsByParentPhoneAndRegParent(String phone, UUID regParent);

    boolean existsByParentEmail(String parentEmail);

    boolean existsByParentPhoneAndRegParent(String phone, UUID regParent);
}

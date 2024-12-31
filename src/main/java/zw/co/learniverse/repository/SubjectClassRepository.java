package zw.co.learniverse.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.learniverse.entities.SubjectClass;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

public interface SubjectClassRepository extends JpaRepository<SubjectClass, Long> {

    Optional<SubjectClass> findByClassIdAndSubjectIdAndLevelIdAndTermIdAndYear(UUID classId, UUID subjectId, UUID levelId, UUID termId, Year year);
}

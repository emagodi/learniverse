package zw.co.learniverse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.learniverse.entities.SchoolClass;


public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    SchoolClass findSchoolClassByNameIgnoreCase(String className);
}

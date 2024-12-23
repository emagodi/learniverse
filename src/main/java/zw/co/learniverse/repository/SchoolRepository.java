package zw.co.learniverse.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.learniverse.entities.School;
import zw.co.learniverse.enums.OwnershipType;
import zw.co.learniverse.enums.Status;


public interface SchoolRepository extends JpaRepository<School, Long> {

    Page<School> findByDistrict(String district, Pageable page);

    Page<School> findByTown(String town, Pageable page);

    Page<School> findByProvince(String province, Pageable page);

    Page<School> findByCountry(String country, Pageable page);

    Page<School> findByStatus(Status status, Pageable page);

    Page<School> findByOwnershipType(OwnershipType ownershipType, Pageable page);

    Long countByTownAndName(String town, String name);

}

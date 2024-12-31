package zw.co.learniverse.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zw.co.learniverse.entities.Parent;

public interface ParentRepository extends JpaRepository<Parent, Long>, JpaSpecificationExecutor<Parent> {


    Parent findParentByUserId(Long userId);

    Parent findByEmail(String email);
}

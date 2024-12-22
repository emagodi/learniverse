package zw.co.learniverse.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import zw.co.learniverse.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
//    @Query("update User u set u.password = ?2 where u.email = ?1")
    @Query("update User u set u.password = ?2, u.temporaryPassword = false where u.email = ?1")
    void updatePasswordAndSetTemporaryFalse(String email, String password);
}
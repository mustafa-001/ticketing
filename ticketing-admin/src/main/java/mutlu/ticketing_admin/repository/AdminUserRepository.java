package mutlu.ticketing_admin.repository;

import mutlu.ticketing_admin.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findUserByEmail(String email);

}

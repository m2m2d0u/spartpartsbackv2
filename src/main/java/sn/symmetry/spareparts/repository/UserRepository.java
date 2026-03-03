package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.User;
import sn.symmetry.spareparts.enums.UserRole;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Page<User> findByRoleAndIsActive(UserRole role, Boolean isActive, Pageable pageable);

    Page<User> findByRole(UserRole role, Pageable pageable);

    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
}

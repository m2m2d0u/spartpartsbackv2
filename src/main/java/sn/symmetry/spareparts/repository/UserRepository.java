package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);

    Page<User> findByRoleCodeAndIsActive(String roleCode, Boolean isActive, Pageable pageable);

    Page<User> findByRoleCode(String roleCode, Pageable pageable);

    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
}

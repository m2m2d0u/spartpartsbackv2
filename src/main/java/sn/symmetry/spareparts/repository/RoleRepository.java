package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByCode(String code);

    List<Role> findByIsActive(Boolean isActive);

    Page<Role> findByIsActive(Boolean isActive, Pageable pageable);

    List<Role> findByIsSystem(Boolean isSystem);

    @Query("SELECT r FROM Role r WHERE r.isActive = true ORDER BY r.displayName")
    List<Role> findAllActiveOrderByDisplayName();

    @Query("SELECT r FROM Role r WHERE r.isActive = true AND r.isSystem = :isSystem ORDER BY r.displayName")
    List<Role> findByIsSystemAndIsActiveOrderByDisplayName(@Param("isSystem") Boolean isSystem);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.code = :code")
    boolean existsByCode(@Param("code") String code);
}

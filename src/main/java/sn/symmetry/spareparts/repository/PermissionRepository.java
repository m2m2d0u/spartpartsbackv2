package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByCode(String code);

    List<Permission> findByCategory(String category);

    List<Permission> findByLevel(String level);

    List<Permission> findByIsActive(Boolean isActive);

    Page<Permission> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE p.category = :category AND p.isActive = true")
    List<Permission> findActiveByCategoryOrderByDisplayName(@Param("category") String category);

    @Query("SELECT p FROM Permission p WHERE p.level = :level AND p.isActive = true")
    List<Permission> findActiveByLevelOrderByDisplayName(@Param("level") String level);

    @Query("SELECT p FROM Permission p WHERE p.isActive = true ORDER BY p.category, p.displayName")
    List<Permission> findAllActiveOrderByCategoryAndDisplayName();

    @Query("SELECT DISTINCT p.category FROM Permission p WHERE p.isActive = true ORDER BY p.category")
    List<String> findAllActiveCategories();

    @Query("SELECT DISTINCT p.level FROM Permission p WHERE p.isActive = true ORDER BY p.level")
    List<String> findAllActiveLevels();
}

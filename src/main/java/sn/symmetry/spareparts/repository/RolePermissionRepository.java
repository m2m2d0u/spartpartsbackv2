package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Permission;
import sn.symmetry.spareparts.entity.RolePermission;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRoleId(UUID roleId);

    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") UUID roleId);

    @Query("SELECT rp.permission.code FROM RolePermission rp WHERE rp.role.id = :roleId")
    List<String> findPermissionCodesByRoleId(@Param("roleId") UUID roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.role.id = :roleId")
    void deleteByRoleId(@Param("roleId") UUID roleId);

    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END " +
           "FROM RolePermission rp " +
           "WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") UUID roleId, @Param("permissionId") UUID permissionId);
}

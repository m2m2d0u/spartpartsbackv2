package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.UserWarehouseRole;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserWarehouseRoleRepository extends JpaRepository<UserWarehouseRole, UUID> {

    List<UserWarehouseRole> findByUserWarehouseId(UUID userWarehouseId);

    @Query("SELECT uwr.role.id FROM UserWarehouseRole uwr WHERE uwr.userWarehouse.id = :userWarehouseId")
    List<UUID> findRoleIdsByUserWarehouseId(@Param("userWarehouseId") UUID userWarehouseId);

    @Query("SELECT DISTINCT p.code " +
           "FROM UserWarehouseRole uwr " +
           "JOIN uwr.role r " +
           "JOIN r.rolePermissions rp " +
           "JOIN rp.permission p " +
           "WHERE uwr.userWarehouse.user.id = :userId " +
           "AND uwr.userWarehouse.warehouse.id = :warehouseId " +
           "AND p.isActive = true")
    List<String> findPermissionCodesByUserAndWarehouse(@Param("userId") UUID userId, @Param("warehouseId") UUID warehouseId);

    @Modifying
    @Query("DELETE FROM UserWarehouseRole uwr WHERE uwr.userWarehouse.id = :userWarehouseId")
    void deleteByUserWarehouseId(@Param("userWarehouseId") UUID userWarehouseId);

    @Query("SELECT CASE WHEN COUNT(uwr) > 0 THEN true ELSE false END " +
           "FROM UserWarehouseRole uwr " +
           "WHERE uwr.userWarehouse.id = :userWarehouseId AND uwr.role.id = :roleId")
    boolean existsByUserWarehouseIdAndRoleId(@Param("userWarehouseId") UUID userWarehouseId, @Param("roleId") UUID roleId);
}

package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.UserWarehouse;
import sn.symmetry.spareparts.enums.WarehousePermission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWarehouseRepository extends JpaRepository<UserWarehouse, UUID> {

    List<UserWarehouse> findByUserId(UUID userId);

    Optional<UserWarehouse> findByUserIdAndWarehouseId(UUID userId, UUID warehouseId);

    void deleteByUserId(UUID userId);

    /**
     * Check if a user has a specific permission for a warehouse.
     *
     * @param userId      the user ID
     * @param warehouseId the warehouse ID
     * @param permission  the permission to check
     * @return true if user has the permission, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(uwp) > 0 THEN true ELSE false END " +
           "FROM UserWarehouse uw JOIN uw.permissions uwp " +
           "WHERE uw.user.id = :userId AND uw.warehouse.id = :warehouseId " +
           "AND uwp.permission = :permission")
    boolean hasPermission(@Param("userId") UUID userId,
                          @Param("warehouseId") UUID warehouseId,
                          @Param("permission") WarehousePermission permission);

    /**
     * Get list of warehouse IDs accessible by a user.
     *
     * @param userId the user ID
     * @return list of warehouse IDs
     */
    @Query("SELECT uw.warehouse.id FROM UserWarehouse uw WHERE uw.user.id = :userId")
    List<UUID> findWarehouseIdsByUserId(@Param("userId") UUID userId);

    List<UserWarehouse> findByWarehouseId(UUID warehouseId);

    void deleteByUserIdAndWarehouseId(UUID userId, UUID warehouseId);
}

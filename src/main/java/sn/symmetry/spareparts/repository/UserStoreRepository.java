package sn.symmetry.spareparts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.UserStore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing UserStore entities.
 * Provides methods to query store assignments for STORE_MANAGER users.
 */
@Repository
public interface UserStoreRepository extends JpaRepository<UserStore, UUID> {

    /**
     * Find all store assignments for a user.
     *
     * @param userId the user ID
     * @return list of user store assignments
     */
    List<UserStore> findByUserId(UUID userId);

    /**
     * Delete all store assignments for a user.
     *
     * @param userId the user ID
     */
    void deleteByUserId(UUID userId);

    /**
     * Check if a user is assigned to a specific store.
     *
     * @param userId the user ID
     * @param storeId the store ID
     * @return true if assigned, false otherwise
     */
    boolean existsByUserIdAndStoreId(UUID userId, UUID storeId);

    /**
     * Get list of store IDs accessible by a user.
     * Used for efficiently filtering data by accessible stores.
     *
     * @param userId the user ID
     * @return list of store IDs
     */
    @Query("SELECT us.store.id FROM UserStore us WHERE us.user.id = :userId")
    List<UUID> findStoreIdsByUserId(@Param("userId") UUID userId);

    List<UserStore> findByStoreId(UUID storeId);

    Optional<UserStore> findByUserIdAndStoreId(UUID userId, UUID storeId);

    void deleteByUserIdAndStoreId(UUID userId, UUID storeId);
}

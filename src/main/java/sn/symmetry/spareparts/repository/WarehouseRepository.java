package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.Warehouse;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    Page<Warehouse> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find warehouses by store ID.
     *
     * @param storeId  the store ID
     * @param pageable pagination info
     * @return page of warehouses
     */
    Page<Warehouse> findByStoreId(UUID storeId, Pageable pageable);

    /**
     * Find warehouses by list of IDs (for filtering by accessible warehouses).
     *
     * @param ids      list of warehouse IDs
     * @param pageable pagination info
     * @return page of warehouses
     */
    Page<Warehouse> findByIdIn(List<UUID> ids, Pageable pageable);

    /**
     * Find warehouses by store ID and list of IDs (combined filter).
     *
     * @param storeId  the store ID
     * @param ids      list of warehouse IDs
     * @param pageable pagination info
     * @return page of warehouses
     */
    Page<Warehouse> findByStoreIdAndIdIn(UUID storeId, List<UUID> ids, Pageable pageable);

    /**
     * Find warehouse IDs for a list of store IDs.
     *
     * @param storeIds list of store IDs
     * @return list of warehouse IDs
     */
    @Query("SELECT w.id FROM Warehouse w WHERE w.store.id IN :storeIds")
    List<UUID> findWarehouseIdsByStoreIds(@Param("storeIds") List<UUID> storeIds);

    /**
     * Find warehouses by list of store IDs.
     *
     * @param storeIds list of store IDs
     * @return list of warehouses
     */
    List<Warehouse> findByStoreIdIn(List<UUID> storeIds);

    Page<Warehouse> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Warehouse> findByNameContainingIgnoreCaseAndIsActive(String name, Boolean isActive, Pageable pageable);

    Page<Warehouse> findByNameContainingIgnoreCaseAndIdIn(String name, List<UUID> ids, Pageable pageable);
}

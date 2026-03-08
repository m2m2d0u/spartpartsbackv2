package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.WarehouseStock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, UUID> {

    Page<WarehouseStock> findByWarehouseId(UUID warehouseId, Pageable pageable);

    Page<WarehouseStock> findByWarehouseIdAndPartId(UUID warehouseId, UUID partId, Pageable pageable);

    Optional<WarehouseStock> findByWarehouseIdAndPartId(UUID warehouseId, UUID partId);

    // Portal — aggregate total stock per part across all warehouses

    @Query("SELECT COALESCE(SUM(ws.quantity), 0) FROM WarehouseStock ws WHERE ws.part.id = :partId")
    int getTotalStockByPartId(@Param("partId") UUID partId);

    @Query("SELECT ws.part.id, COALESCE(SUM(ws.quantity), 0) FROM WarehouseStock ws WHERE ws.part.id IN :partIds GROUP BY ws.part.id")
    List<Object[]> getTotalStockByPartIds(@Param("partIds") List<UUID> partIds);

    // Portal — stock from specific warehouse only

    @Query("SELECT COALESCE(ws.quantity, 0) FROM WarehouseStock ws WHERE ws.part.id = :partId AND ws.warehouse.id = :warehouseId")
    int getStockByPartIdAndWarehouseId(@Param("partId") UUID partId, @Param("warehouseId") UUID warehouseId);

    @Query("SELECT ws.part.id, COALESCE(ws.quantity, 0) FROM WarehouseStock ws WHERE ws.part.id IN :partIds AND ws.warehouse.id = :warehouseId")
    List<Object[]> getStockByPartIdsAndWarehouseId(@Param("partIds") List<UUID> partIds, @Param("warehouseId") UUID warehouseId);

    // Dashboard — unfiltered (admin)

    @Query("SELECT COUNT(DISTINCT ws.part.id) FROM WarehouseStock ws WHERE ws.quantity > 0")
    long countDistinctPartsInStockAll();

    @Query("SELECT COALESCE(SUM(ws.quantity * ws.part.purchasePrice), 0) FROM WarehouseStock ws WHERE ws.quantity > 0")
    BigDecimal calculateTotalStockValueAll();

    @Query("SELECT COUNT(ws) FROM WarehouseStock ws WHERE ws.quantity <= ws.minStockLevel AND ws.quantity >= 0")
    long countLowStockItemsAll();

    @Query("SELECT ws FROM WarehouseStock ws JOIN FETCH ws.part JOIN FETCH ws.warehouse WHERE ws.quantity <= ws.minStockLevel AND ws.quantity >= 0 ORDER BY (ws.quantity - ws.minStockLevel) ASC")
    List<WarehouseStock> findLowStockItemsAll(Pageable pageable);

    @Query("SELECT ws.warehouse.name, COALESCE(SUM(ws.quantity * ws.part.purchasePrice), 0) FROM WarehouseStock ws WHERE ws.quantity > 0 GROUP BY ws.warehouse.id, ws.warehouse.name")
    List<Object[]> getStockValueByWarehouseAll();

    // Dashboard — filtered by warehouse IDs

    @Query("SELECT COUNT(DISTINCT ws.part.id) FROM WarehouseStock ws WHERE ws.quantity > 0 AND ws.warehouse.id IN :warehouseIds")
    long countDistinctPartsInStockByWarehouses(@Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT COALESCE(SUM(ws.quantity * ws.part.purchasePrice), 0) FROM WarehouseStock ws WHERE ws.quantity > 0 AND ws.warehouse.id IN :warehouseIds")
    BigDecimal calculateTotalStockValueByWarehouses(@Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT COUNT(ws) FROM WarehouseStock ws WHERE ws.quantity <= ws.minStockLevel AND ws.quantity >= 0 AND ws.warehouse.id IN :warehouseIds")
    long countLowStockItemsByWarehouses(@Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT ws FROM WarehouseStock ws JOIN FETCH ws.part JOIN FETCH ws.warehouse WHERE ws.quantity <= ws.minStockLevel AND ws.quantity >= 0 AND ws.warehouse.id IN :warehouseIds ORDER BY (ws.quantity - ws.minStockLevel) ASC")
    List<WarehouseStock> findLowStockItemsByWarehouses(@Param("warehouseIds") List<UUID> warehouseIds, Pageable pageable);

    @Query("SELECT ws.warehouse.name, COALESCE(SUM(ws.quantity * ws.part.purchasePrice), 0) FROM WarehouseStock ws WHERE ws.quantity > 0 AND ws.warehouse.id IN :warehouseIds GROUP BY ws.warehouse.id, ws.warehouse.name")
    List<Object[]> getStockValueByWarehouseFiltered(@Param("warehouseIds") List<UUID> warehouseIds);
}

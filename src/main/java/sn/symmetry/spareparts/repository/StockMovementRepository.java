package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.StockMovement;
import sn.symmetry.spareparts.enums.StockMovementType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findByWarehouseId(UUID warehouseId, Pageable pageable);

    Page<StockMovement> findByPartId(UUID partId, Pageable pageable);

    Page<StockMovement> findByWarehouseIdAndPartId(UUID warehouseId, UUID partId, Pageable pageable);

    Page<StockMovement> findByType(StockMovementType type, Pageable pageable);

    // Dashboard — unfiltered

    @Query("SELECT COUNT(sm) FROM StockMovement sm WHERE sm.createdAt >= :since")
    long countMovementsSinceAll(@Param("since") LocalDateTime since);

    @Query("SELECT CAST(sm.createdAt AS LocalDate), SUM(CASE WHEN sm.quantityChange > 0 THEN sm.quantityChange ELSE 0 END), SUM(CASE WHEN sm.quantityChange < 0 THEN ABS(sm.quantityChange) ELSE 0 END) FROM StockMovement sm WHERE sm.createdAt >= :since GROUP BY CAST(sm.createdAt AS LocalDate) ORDER BY CAST(sm.createdAt AS LocalDate)")
    List<Object[]> dailyMovementsSinceAll(@Param("since") LocalDateTime since);

    @Query("SELECT sm FROM StockMovement sm JOIN FETCH sm.part JOIN FETCH sm.warehouse ORDER BY sm.createdAt DESC")
    List<StockMovement> findRecentMovementsAll(Pageable pageable);

    // Dashboard — filtered by warehouse IDs

    @Query("SELECT COUNT(sm) FROM StockMovement sm WHERE sm.createdAt >= :since AND sm.warehouse.id IN :warehouseIds")
    long countMovementsSinceByWarehouses(@Param("since") LocalDateTime since, @Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT CAST(sm.createdAt AS LocalDate), SUM(CASE WHEN sm.quantityChange > 0 THEN sm.quantityChange ELSE 0 END), SUM(CASE WHEN sm.quantityChange < 0 THEN ABS(sm.quantityChange) ELSE 0 END) FROM StockMovement sm WHERE sm.createdAt >= :since AND sm.warehouse.id IN :warehouseIds GROUP BY CAST(sm.createdAt AS LocalDate) ORDER BY CAST(sm.createdAt AS LocalDate)")
    List<Object[]> dailyMovementsSinceByWarehouses(@Param("since") LocalDateTime since, @Param("warehouseIds") List<UUID> warehouseIds);

    @Query("SELECT sm FROM StockMovement sm JOIN FETCH sm.part JOIN FETCH sm.warehouse WHERE sm.warehouse.id IN :warehouseIds ORDER BY sm.createdAt DESC")
    List<StockMovement> findRecentMovementsByWarehouses(@Param("warehouseIds") List<UUID> warehouseIds, Pageable pageable);
}

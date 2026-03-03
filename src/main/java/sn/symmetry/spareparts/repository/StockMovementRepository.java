package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.StockMovement;
import sn.symmetry.spareparts.enums.StockMovementType;

import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findByWarehouseId(UUID warehouseId, Pageable pageable);

    Page<StockMovement> findByPartId(UUID partId, Pageable pageable);

    Page<StockMovement> findByWarehouseIdAndPartId(UUID warehouseId, UUID partId, Pageable pageable);

    Page<StockMovement> findByType(StockMovementType type, Pageable pageable);
}

package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.StockMovement;
import sn.symmetry.spareparts.enums.StockMovementType;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findByWarehouseId(Long warehouseId, Pageable pageable);

    Page<StockMovement> findByPartId(Long partId, Pageable pageable);

    Page<StockMovement> findByWarehouseIdAndPartId(Long warehouseId, Long partId, Pageable pageable);

    Page<StockMovement> findByType(StockMovementType type, Pageable pageable);
}

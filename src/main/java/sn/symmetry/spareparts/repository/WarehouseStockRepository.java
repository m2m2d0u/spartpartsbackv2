package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.WarehouseStock;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, UUID> {

    Page<WarehouseStock> findByWarehouseId(UUID warehouseId, Pageable pageable);

    Page<WarehouseStock> findByWarehouseIdAndPartId(UUID warehouseId, UUID partId, Pageable pageable);

    Optional<WarehouseStock> findByWarehouseIdAndPartId(UUID warehouseId, UUID partId);
}

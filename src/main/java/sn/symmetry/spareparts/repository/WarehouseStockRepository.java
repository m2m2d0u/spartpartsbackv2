package sn.symmetry.spareparts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.symmetry.spareparts.entity.WarehouseStock;

import java.util.Optional;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {

    Page<WarehouseStock> findByWarehouseId(Long warehouseId, Pageable pageable);

    Page<WarehouseStock> findByWarehouseIdAndPartId(Long warehouseId, Long partId, Pageable pageable);

    Optional<WarehouseStock> findByWarehouseIdAndPartId(Long warehouseId, Long partId);
}
